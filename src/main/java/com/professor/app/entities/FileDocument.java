package com.professor.app.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Document(collection = "files")
public class FileDocument {

    @Id
    private String id;

    private String userId;

    private String name;

    private String fileType;

    private long size;

    @Field("content")
    private byte[] content;


}
