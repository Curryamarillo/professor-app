package com.professor.app.controllers;

import com.professor.app.entities.FileDocument;
import com.professor.app.exceptions.StorageFileNotFoundException;
import com.professor.app.services.FileDocumentService;
import lombok.AllArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/files")
@AllArgsConstructor
public class FileController {

    private FileDocumentService fileDocumentService;

    @GetMapping
    public ResponseEntity<List<String>> listUploadedFiles(String userId) {
        List<FileDocument> files = fileDocumentService.getUserFilesByUserId(userId);

        List<String> fileNames = files.stream()
                .map(file -> "/api/files/" + file.getFileName())
                .toList();
        return ResponseEntity.status(HttpStatus.OK).body(fileNames);
    }

    @GetMapping("/{filename:.+}")
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
        try {
            Resource file = fileDocumentService.loadFileAsAResource(filename);
            if (file == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
                    .body(file);
        } catch (StorageFileNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }

    }
    @PostMapping
    public ResponseEntity<String> handleFileUpload(@RequestParam("file") MultipartFile file) {
        try {
            FileDocument savedFile = fileDocumentService.storeFile(file);
            return ResponseEntity.ok("File successfully uploaded " + savedFile.getFileName());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Failed to upload file");
        }
    }

}






