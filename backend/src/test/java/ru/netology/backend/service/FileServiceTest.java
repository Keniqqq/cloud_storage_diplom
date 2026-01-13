package ru.netology.backend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.backend.exception.FileNotfoundException;
import ru.netology.backend.model.FileInfo;
import ru.netology.backend.repository.FileRepository;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FileServiceTest {

    @Mock
    private FileRepository fileRepository;

    @Captor
    private ArgumentCaptor<FileInfo> fileInfoCaptor;

    private FileService fileService;

    @BeforeEach
    void setUp() {
        fileService = new FileService(fileRepository);
    }

    @Test
    void shouldSaveFile() throws IOException {
        Long userId = 1L;
        MultipartFile multipartFile = new MockMultipartFile("test.txt", "test content".getBytes());

        fileService.saveFile(userId, multipartFile);

        verify(fileRepository, times(1)).save(fileInfoCaptor.capture());
        FileInfo saved = fileInfoCaptor.getValue();
        assertEquals("test.txt", saved.getFilename());
        assertEquals(userId, saved.getUserId());
    }

    @Test
    void shouldListFiles() {
        Long userId = 1L;
        FileInfo file = new FileInfo("test.txt", 100L, "/tmp/test.txt", userId);
        when(fileRepository.findByUserId(userId)).thenReturn(List.of(file));

        List<String> files = fileService.listFiles(userId);

        assertEquals(1, files.size());
        assertEquals("test.txt", files.get(0));
    }

    @Test
    void shouldDeleteFile() throws IOException, FileNotfoundException {
        Long userId = 1L;
        String filename = "test.txt";
        FileInfo file = new FileInfo(filename, 100L, "/tmp/test.txt", userId);
        when(fileRepository.findByUserIdAndFilename(userId, filename)).thenReturn(file);

        fileService.deleteFile(userId, filename);

        verify(fileRepository, times(1)).delete(file);
    }

    @Test
    void shouldThrowExceptionWhenFileNotFoundToDelete() {
        Long userId = 1L;
        String filename = "nonexistent.txt";
        when(fileRepository.findByUserIdAndFilename(userId, filename)).thenReturn(null);

        assertThrows(FileNotfoundException.class, () -> fileService.deleteFile(userId, filename));
    }

    @Test
    void shouldRenameFile() throws IOException, FileNotfoundException {
        Long userId = 1L;
        String oldFilename = "old.txt";
        String newFilename = "new.txt";
        FileInfo file = new FileInfo(oldFilename, 100L, "/tmp/old.txt", userId);
        when(fileRepository.findByUserIdAndFilename(userId, oldFilename)).thenReturn(file);

        fileService.renameFile(userId, oldFilename, newFilename);

        verify(fileRepository, times(1)).save(file);
        assertEquals(newFilename, file.getFilename());
    }

    @Test
    void shouldThrowExceptionWhenFileNotFoundToRename() {
        Long userId = 1L;
        String oldFilename = "old.txt";
        String newFilename = "new.txt";
        when(fileRepository.findByUserIdAndFilename(userId, oldFilename)).thenReturn(null);

        assertThrows(FileNotfoundException.class, () ->
                fileService.renameFile(userId, oldFilename, newFilename));
    }
}