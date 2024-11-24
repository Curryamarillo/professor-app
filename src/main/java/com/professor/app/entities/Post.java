package com.professor.app.entities;

import com.professor.app.roles.Role;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Set;

@Document(collection = "post")
public class Post {

    @Id
    private String id;

    private List<String> postAuthorId;

    private FileDocument attachment;

    private String comments;

    private Role postedByRole;

    private Set<String> hashtags;
}
