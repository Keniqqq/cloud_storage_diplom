package ru.netology.backend.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.backend.exception.FileNotfoundException;
import ru.netology.backend.model.FileInfo;
import ru.netology.backend.repository.FileRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
@Transactional
public class FileService {
    private final FileRepository fileRepository;
    private final String uploadDir = System.getProperty("java.io.tmpdir");
    private final Logger logger = LoggerFactory.getLogger(FileService.class);

    public FileService(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    public List<String> listFiles(Long userId) {
        List<FileInfo> files = fileRepository.findByUserId(userId);
        return files.stream()
                .map(FileInfo::getFilename)
                .toList();
    }

    public void saveFile(Long userId, MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            originalFilename = "unknown";
        }

        Path filePath = Paths.get(uploadDir, originalFilename);
        try {
            Files.write(filePath, file.getBytes());
        } catch (IOException e) {
            logger.error("Could not save file: {}", originalFilename, e);
            throw e;
        }

        FileInfo fileInfo = new FileInfo();
        fileInfo.setFilename(originalFilename);
        fileInfo.setSize(file.getSize());
        fileInfo.setPath(filePath.toString());
        fileInfo.setUserId(userId);
        fileRepository.save(fileInfo);

        logger.info("File saved: {} for user ID: {}", originalFilename, userId);
    }

    public void deleteFile(Long userId, String filename) throws IOException, FileNotfoundException {
        FileInfo file = fileRepository.findByUserIdAndFilename(userId, filename);
        if (file == null) {
            throw new FileNotfoundException("File not found: " + filename);
        }
        Path path = Paths.get(file.getPath());
        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            logger.error("Could not delete file: {}", path, e);
            throw e;
        }
        fileRepository.delete(file);
        logger.info("File deleted: {} for user ID: {}", filename, userId);
    }

    public void renameFile(Long userId, String oldFilename, String newFilename) throws IOException, FileNotfoundException {
        FileInfo file = fileRepository.findByUserIdAndFilename(userId, oldFilename);
        if (file == null) {
            throw new FileNotfoundException("File not found: " + oldFilename);
        }

        Path oldPath = Paths.get(file.getPath());
        Path newPath = Paths.get(oldPath.getParent().toString(), newFilename);

        try {
            Files.move(oldPath, newPath);
        } catch (IOException e) {
            logger.error("Could not rename file: {} to {}", oldFilename, newFilename, e);
            throw e;
        }

        file.setFilename(newFilename);
        file.setPath(newPath.toString());
        fileRepository.save(file);

        logger.info("File renamed: {} -> {} for user ID: {}", oldFilename, newFilename, userId);
    }
}