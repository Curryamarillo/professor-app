package com.professor.app.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Document(collection = "users")
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class Assistant extends User{

    private List<String> courseId;

    private Set<String> duties;

}
