package ru.netology.backend.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.netology.backend.model.User;
import ru.netology.backend.repository.UserRepository;
import ru.netology.backend.service.FileService;
import ru.netology.backend.service.JwtService;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FileController.class)
class FileControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FileService fileService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserRepository userRepository;

    @Test
    void shouldReturnFileList() throws Exception {
        when(jwtService.validateToken("valid-token")).thenReturn(true);
        when(jwtService.extractUsername("valid-token")).thenReturn("user");
        when(userRepository.findByLogin("user")).thenReturn(java.util.Optional.of(new User()));
        when(fileService.listFiles(anyLong())).thenReturn(java.util.List.of("file1.txt"));

        mockMvc.perform(get("/list")
                        .header("auth-token", "valid-token"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldUploadFile() throws Exception {
        when(jwtService.validateToken("valid-token")).thenReturn(true);
        when(jwtService.extractUsername("valid-token")).thenReturn("user");
        when(userRepository.findByLogin("user")).thenReturn(java.util.Optional.of(new User()));

        mockMvc.perform(multipart("/file")
                        .file("filename", "content".getBytes())
                        .header("auth-token", "valid-token"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldRenameFile() throws Exception {
        when(jwtService.validateToken("valid-token")).thenReturn(true);
        when(jwtService.extractUsername("valid-token")).thenReturn("user");
        when(userRepository.findByLogin("user")).thenReturn(java.util.Optional.of(new User()));

        mockMvc.perform(put("/file")
                        .param("filename", "old.txt")
                        .param("newFilename", "new.txt")
                        .header("auth-token", "valid-token"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldDeleteFile() throws Exception {
        when(jwtService.validateToken("valid-token")).thenReturn(true);
        when(jwtService.extractUsername("valid-token")).thenReturn("user");
        when(userRepository.findByLogin("user")).thenReturn(java.util.Optional.of(new User()));

        mockMvc.perform(delete("/file")
                        .param("filename", "file.txt")
                        .header("auth-token", "valid-token"))
                .andExpect(status().isOk());
    }
}