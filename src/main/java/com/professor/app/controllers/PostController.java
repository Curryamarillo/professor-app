package com.professor.app.controllers;

import com.professor.app.dto.posts.PostRequestDTO;
import com.professor.app.entities.Post;
import com.professor.app.services.PostService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/post")
@AllArgsConstructor
public class PostController {

    private PostService postService;

    @PostMapping("/create")
    public ResponseEntity<Post> createPost(@RequestBody PostRequestDTO postRequestDTO) {
        Post createdPost = postService.createPost(postRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPost);
    }
}
