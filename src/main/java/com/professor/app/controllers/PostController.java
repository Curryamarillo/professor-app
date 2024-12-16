package com.professor.app.controllers;

import com.professor.app.dto.posts.CommentCreateRequestDTO;
import com.professor.app.dto.posts.CommentDeleteDTO;
import com.professor.app.dto.posts.PostRequestDTO;
import com.professor.app.entities.Comment;
import com.professor.app.entities.Post;
import com.professor.app.services.PostService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    @GetMapping("/{id}")
    public ResponseEntity<Post> getPostById(@PathVariable String id) {
        Post post = postService.getPost(id);
        return ResponseEntity.status(HttpStatus.OK).body(post);
    }
    @PutMapping("/update/{id}")
    public ResponseEntity<String> updatePost(@PathVariable String id, @RequestParam String title, @RequestParam String textContent) {
        String response = postService.updatePost(id, title, textContent);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deletePost(@PathVariable String id) {
        String response = postService.deletePost(id);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
    @PostMapping("/add-hashtag/{id}")
    public ResponseEntity<String> addHashtag(@PathVariable String id, @RequestParam String hashtag) {
        String response = postService.addHashtags(id, hashtag);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
    @DeleteMapping("/delete-hashtag/{id}")
    public ResponseEntity<String> removeHashtag(@PathVariable String id, @RequestParam String hashtag) {
        String response = postService.deleteHashtag(id, hashtag);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
    @PostMapping("/add-comments/{id}")
    public ResponseEntity<String> addComments(@PathVariable String id, @RequestBody CommentCreateRequestDTO comments) {
        String response = postService.addComments(id, comments);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
    @DeleteMapping("/delete-comments/{id}")
    public ResponseEntity<String> deleteComments(@PathVariable String id,@RequestParam String commentId) {
        String response = postService.deleteComment(id, commentId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
    @GetMapping("/hashtag/{hashtag}")
    public ResponseEntity<List<Post>> getPostByHashtag(@PathVariable String hashtag) {
        List<Post> response = postService.getPostsByHashTag(hashtag);
        return  ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
