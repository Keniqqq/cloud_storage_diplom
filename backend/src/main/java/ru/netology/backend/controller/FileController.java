package ru.netology.backend.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.backend.exception.FileNotfoundException;
import ru.netology.backend.exception.UnauthorizedException;
import ru.netology.backend.model.User;
import ru.netology.backend.repository.UserRepository;
import ru.netology.backend.service.FileService;
import ru.netology.backend.service.JwtService;

import java.util.List;

@RestController
public class FileController {
    private final FileService fileService;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final Logger log = LoggerFactory.getLogger(FileController.class);

    public FileController(FileService fileService, JwtService jwtService, UserRepository userRepository) {
        this.fileService = fileService;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    @GetMapping("/list")
    public ResponseEntity<List<String>> list(@RequestHeader("auth-token") String token) throws UnauthorizedException {
        if (!jwtService.validateToken(token)) {
            throw new UnauthorizedException("Invalid token");
        }
        String username = jwtService.extractUsername(token);
        Long userId = getUserIdByUsername(username);
        List<String> files = fileService.listFiles(userId);
        log.info("User {} requested file list", username);
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
        log.info("User {} uploaded file: {}", username, file.getOriginalFilename());
        return ResponseEntity.ok().build();
    }

    @PutMapping("/file")
    public ResponseEntity<?> renameFile(
            @RequestHeader("auth-token") String token,
            @RequestParam("filename") String oldFilename,
            @RequestParam("newFilename") String newFilename) throws Exception, UnauthorizedException, FileNotfoundException {
        if (!jwtService.validateToken(token)) {
            throw new UnauthorizedException("Invalid token");
        }
        String username = jwtService.extractUsername(token);
        Long userId = getUserIdByUsername(username);
        fileService.renameFile(userId, oldFilename, newFilename);
        log.info("User {} renamed file: {} -> {}", username, oldFilename, newFilename);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/file")
    public ResponseEntity<?> delete(@RequestHeader("auth-token") String token,
                                    @RequestParam("filename") String filename) throws Exception, UnauthorizedException, FileNotfoundException {
        if (!jwtService.validateToken(token)) {
            throw new UnauthorizedException("Invalid token");
        }
        String username = jwtService.extractUsername(token);
        Long userId = getUserIdByUsername(username);
        fileService.deleteFile(userId, filename);
        log.info("User {} deleted file: {}", username, filename);
        return ResponseEntity.ok().build();
    }

    private Long getUserIdByUsername(String username) {
        User user = userRepository.findByLogin(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username)); // или кастомное исключение
        return user.getId();
    }
}