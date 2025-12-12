package ru.netology.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.backend.service.FileService;

import java.util.List;

@RestController
public class FileController {
    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @GetMapping("/list")
    public ResponseEntity<List<String>> list(@RequestHeader("auth-token") String token) {
        return ResponseEntity.ok(fileService.listFiles(token));
    }

    @PostMapping("/file")
    public ResponseEntity<?> upload(@RequestHeader("auth-token") String token,
                                    @RequestParam("filename") MultipartFile file) {
        fileService.saveFile(token, file);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/file")
    public ResponseEntity<?> delete(@RequestHeader("auth-token") String token,
                                    @RequestParam("filename") String filename) {
        fileService.deleteFile(token, filename);
        return ResponseEntity.ok().build();
    }
}