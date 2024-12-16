package com.professor.app.entities;

import com.professor.app.dto.posts.CommentCreateRequestDTO;
import com.professor.app.roles.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Document(collection = "post")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Post {

    @Id
    private String id;

    private Set<String> postAuthorId;

    private String title;

    private String textContent;

    private List<Comment> comments = new ArrayList<>();

    private Role postedByRole;

    private Set<String> hashtags;

    @CreatedDate
    private LocalDateTime createdAt;

    public void addComment(Comment comment) {
        this.comments.add(comment);
    }

}
