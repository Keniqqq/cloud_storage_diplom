package ru.netology.backend.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.netology.backend.service.FileService;
import ru.netology.backend.service.JwtService;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FileController.class)
class FileControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FileService fileService;

    @MockBean
    private JwtService jwtService;

    @Test
    void shouldReturnFileList() throws Exception {
        when(jwtService.validateToken("valid-token")).thenReturn(true);
        when(jwtService.extractUsername("valid-token")).thenReturn("user");
        when(fileService.listFiles(anyLong())).thenReturn(List.of("file1.txt"));

        mockMvc.perform(get("/list")
                        .header("auth-token", "valid-token"))
                .andExpect(status().isOk());
    }
}