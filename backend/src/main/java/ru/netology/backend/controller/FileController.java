package ru.netology.backend.controller;

import org.junit.platform.commons.logging.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.testcontainers.shaded.com.trilead.ssh2.log.Logger;
import ru.netology.backend.service.FileService;
import ru.netology.backend.dto.FileMetadata;
import ru.netology.backend.exception.UnauthorizedException;
import ru.netology.backend.service.FileService;
import ru.netology.backend.service.JwtService;

import java.util.List;

@RestController
public class FileController {
    private final FileService fileService;
    private final JwtService jwtService;
    private final Logger logger = (Logger) (Logger) LoggerFactory.getLogger(FileController.class);

    public FileController(FileService fileService, JwtService jwtService) {
        this.fileService = fileService;
        this.jwtService = jwtService;
    }

    @GetMapping("/list")
    public ResponseEntity<List<String>> list(@RequestHeader("auth-token") String token) {
        if (!jwtService.validateToken(token)) {
            try {
                throw new UnauthorizedException("Invalid token");
            } catch (UnauthorizedException e) {
                throw new RuntimeException(e);
            }
        }
        String username = jwtService.extractUsername(token);
        Long userId = getUserIdByUsername(username); // можно вынести в отдельный сервис
        List<String> files = fileService.listFiles(userId);
        logger.log(1, username);
        return ResponseEntity.ok(files);
    }


    @PostMapping("/file")
    public ResponseEntity<?> upload(@RequestHeader("auth-token") String token,
                                    @RequestParam("filename") MultipartFile file) throws Exception, UnauthorizedException {
        if (!jwtService.validateToken(token)) {
            throw new UnauthorizedException("Invalid token");
        }
        String username = jwtService.extractUsername(token);
        Long userId = getUserIdByUsername(username);
        fileService.saveFile(userId, file);
        logger.log(2, file.getOriginalFilename());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/file")
    public ResponseEntity<?> delete(@RequestHeader("auth-token") String token,
                                    @RequestParam("filename") String filename) throws Exception, UnauthorizedException {
        if (!jwtService.validateToken(token)) {
            throw new UnauthorizedException("Invalid token");
        }
        String username = jwtService.extractUsername(token);
        Long userId = getUserIdByUsername(username);
        fileService.deleteFile(userId, filename);
        logger.log(3, filename);
        return ResponseEntity.ok().build();
    }

    private Long getUserIdByUsername(String username) {
        return 1L;
    }
}