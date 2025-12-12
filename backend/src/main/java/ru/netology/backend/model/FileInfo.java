package ru.netology.backend.model;

import jakarta.persistence.*;

@Entity
@Table(name = "files")
public class FileInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String filename;
    private Long size;
    private String path;
    private Long userId;

    public FileInfo() {}

    public FileInfo(String filename, Long size, String path, Long userId) {
        this.filename = filename;
        this.size = size;
        this.path = path;
        this.userId = userId;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFilename() { return filename; }
    public void setFilename(String filename) { this.filename = filename; }

    public Long getSize() { return size; }
    public void setSize(Long size) { this.size = size; }

    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
}