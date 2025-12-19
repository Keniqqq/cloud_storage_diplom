package ru.netology.backend.service;

import org.junit.platform.commons.logging.Logger;
import org.junit.platform.commons.logging.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.backend.exception.FileNotfoundException;
import ru.netology.backend.model.FileInfo;
import ru.netology.backend.model.Token;
import ru.netology.backend.repository.FileRepository;
import ru.netology.backend.repository.TokenRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

@Service
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
        Path filePath = Paths.get(uploadDir, file.getOriginalFilename());
        try {
            Files.write(filePath, file.getBytes());
        } catch (IOException e) {
            logger.error("Could not save file: {}", file.getOriginalFilename(), e);
            throw e; // проброс ошибки для отката
        }
        FileInfo fileInfo = new FileInfo();
        fileInfo.setFilename(file.getOriginalFilename());
        fileInfo.setSize(file.getSize());
        fileInfo.setPath(filePath.toString());
        fileInfo.setUserId(userId);
        fileRepository.save(fileInfo);
        logger.info("File saved: {} for user ID: {}", file.getOriginalFilename(), userId);
    }

    public void deleteFile(Long userId, String filename) throws IOException {
        FileInfo file = fileRepository.findByUserIdAndFilename(userId, filename);
        if (file == null) {
            throw new FileNotFoundException("File not found: " + filename);
        }
        Path path = Paths.get(file.getPath());
        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            logger.error("Could not delete file: {}", path, e);
            throw e; // проброс ошибки для отката
        }
        fileRepository.delete(file);
        logger.info("File deleted: {} for user ID: {}", filename, userId);
    }
}