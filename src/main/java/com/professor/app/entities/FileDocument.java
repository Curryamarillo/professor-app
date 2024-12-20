package com.professor.app.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Document(collection = "file_documents")
@Builder
public class FileDocument {

    @Id
    private String id;

    private String userId;

    private String fileName;

    private String mimeType;

    private long size;

    private String downloadUrl;

    @CreatedDate
    private LocalDateTime createdAt;
}
