package com.professor.app.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Set;

@Document(collection = "courses")
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Course {

    @Id
    private String id;

    @Indexed(unique = true)
    private String code;

    private String name;

    private String comments;

    private Set<String> studentsId;
}
