package com.professor.app.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@Document(collection = "users")
public class Professor extends User{

    private List<String> courseIds;

    private List<String> courseNames;

    private List<String> studentsIds;
}
