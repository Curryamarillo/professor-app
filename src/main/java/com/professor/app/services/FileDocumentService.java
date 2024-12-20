package com.professor.app.services;

import com.professor.app.entities.FileDocument;
import com.professor.app.entities.User;
import com.professor.app.exceptions.UserNotFoundException;
import com.professor.app.repositories.FileDocumentRepository;
import com.professor.app.repositories.UserRepository;
import com.professor.app.services.storage.FileSystemStorageService;
import lombok.AllArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Service
@AllArgsConstructor
public class FileDocumentService {

    private FileSystemStorageService fileSystemStorageService;

    private FileDocumentRepository fileDocumentRepository;

    private UserRepository userRepository;


    public FileDocument storeFile(MultipartFile file) {
        try {
            String username = getUsername();
            fileSystemStorageService.store(file);

            FileDocument fileDocument = FileDocument.builder()
                    .userId(username)
                    .fileName(file.getOriginalFilename())
                    .mimeType(file.getContentType())
                    .size(file.getSize())
                    .downloadUrl("/files/download/" + file.getOriginalFilename())
                    .createdAt(LocalDateTime.now())
                    .build();
            return fileDocumentRepository.save(fileDocument);
        }  catch (Exception e) {
            throw new RuntimeException("Error storing file", e);
        }
    }
    public Resource loadFileAsAResource(String filename) {
        return fileSystemStorageService.loadAsResource(filename);
    }

    public List<FileDocument> getUserFiles() {
        String username = getUsername();
        Optional<User> user = userRepository.findByEmail(username);
        if (user.isPresent()) {
            String userId = user.get().getId();
            return fileDocumentRepository.findByUserId(userId);
        } else {
            throw new UserNotFoundException("User not found:");
        }
    }
    public List<FileDocument> getUserFilesByUserId(String userId) {
        return fileDocumentRepository.findByUserId(userId);
    }



    ///  Method to get username from Security Context
    private String getUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("User is not authenticated");
        }
        return authentication.getName();
    }

}
