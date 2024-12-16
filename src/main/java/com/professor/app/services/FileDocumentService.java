package com.professor.app.services;

import com.professor.app.services.storage.FileSystemStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


@Service
@RequiredArgsConstructor
public class FileDocumentService {

    private final FileSystemStorageService fileSystemStorageService;

    private FileDocumentService fileDocumentService;

    public String uploadFile(MultipartFile file, String userId) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Cannot store an empty file.");
        }

    // TODO complete method with UserDetails Service from Spring Security
        return "File uploaded successfully";
    }

}
