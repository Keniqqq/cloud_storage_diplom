package ru.netology.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.netology.backend.model.FileInfo;

import java.util.List;

public interface FileRepository extends JpaRepository<FileInfo, Long> {
    List<FileInfo> findByUserId(Long userId, String filename);
}