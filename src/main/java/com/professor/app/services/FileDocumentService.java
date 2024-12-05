package com.professor.app.services;

import com.professor.app.services.storage.FileSystemStorageService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileDocumentService {

    private final FileSystemStorageService fileSystemStorageService;

    public String uploadFile(MultipartFile file, String userId) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Cannot store an empty file.");
        }
        String uuidFileName = UUID.randomUUID() + "_" + file.getOriginalFilename();


        return "File uploaded successfully";
    }

}
