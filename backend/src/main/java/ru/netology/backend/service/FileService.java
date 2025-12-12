package ru.netology.backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
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
    private final TokenRepository tokenRepository;
    private final String uploadDir = System.getProperty("java.io.tmpdir");

    @Value("${filename}")
    private String filename;

    public FileService(FileRepository fileRepository, TokenRepository tokenRepository) {
        this.fileRepository = fileRepository;
        this.tokenRepository = tokenRepository;
    }

    public List<String> listFiles(String token) {
        Long userId = getUserIdByToken(token);
        List<FileInfo> files = fileRepository.findByUserId(userId, filename);
        return files.stream()
                .map(FileInfo::getFilename)
                .collect(Collectors.toList());
    }

    public void saveFile(String token, MultipartFile file) {
        Long userId = getUserIdByToken(token);
        Path filePath = Paths.get(uploadDir, file.getOriginalFilename());
        try {
            Files.write(filePath, file.getBytes());
        } catch (IOException e) {
            throw new RuntimeException("Could not save file", e);
        }
        FileInfo fileInfo = new FileInfo();
        fileInfo.setFilename(file.getOriginalFilename());
        fileInfo.setSize(file.getSize());
        fileInfo.setPath(filePath.toString());
        fileInfo.setUserId(userId);
        fileRepository.save(fileInfo);
    }

    public void deleteFile(String token, String filename) {
        Long userId = getUserIdByToken(token);
        FileInfo file = (FileInfo) fileRepository.findByUserId(userId, filename);
        if (file != null) {
            Path path = Paths.get(file.getPath());
            try {
                Files.deleteIfExists(path);
            } catch (IOException e) {
                throw new RuntimeException("Could not delete file", e);
            }
            fileRepository.delete(file);
        }
    }

    private Long getUserIdByToken(String token) {
        Token tokenEntity = tokenRepository.findByTokenValueAndActiveTrue(token)
                .orElseThrow(() -> new RuntimeException("Invalid token"));
        return tokenEntity.getUserId();
    }
}