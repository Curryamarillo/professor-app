package com.professor.app.controllers;

import com.professor.app.exceptions.StorageFileNotFoundException;
import com.professor.app.services.storage.StorageService;
import lombok.AllArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/upload")
@AllArgsConstructor
public class FileUploadController {

    private final StorageService storageService;

    @GetMapping
    public ResponseEntity<List<String>> listUploadedFiles() {
        List<String> files = storageService.loadAll().map(
                        path -> "/api/files/" + path.getFileName().toString())
                .collect(Collectors.toList());

        return ResponseEntity.ok(files);
    }

    @GetMapping("/{filename:.+}")
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
        try {
            Resource file = storageService.loadAsResource(filename);
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
            storageService.store(file);
            return ResponseEntity.ok("File successfully uploaded: " + file.getOriginalFilename());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Failed to upload file");
        }
    }
}
