package com.professor.app.services;

import com.professor.app.dto.posts.PostRequestDTO;
import com.professor.app.entities.Comment;
import com.professor.app.entities.Post;
import com.professor.app.exceptions.PostNotFoundException;
import com.professor.app.repositories.PostRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

@Service
@AllArgsConstructor
public class PostService {

    private final PostRepository postRepository;


    public Post createPost(PostRequestDTO postRequestDTO) {
        Post post = Post.builder()
                .postAuthorId(postRequestDTO.postAuthor())
                .title(postRequestDTO.title())
                .postedByRole(postRequestDTO.postedByRole())
                .textContent(postRequestDTO.textContent())
                .createdAt(new Date(System.currentTimeMillis()))
                .build();
        return postRepository.save(post);
    }
    public Post getPost(String id) {
        return findPostById(id);
    }
    public String updatePost(String id, String title, String textContent) {
        Post post = findPostById(id);
        post.setTitle(title);
        post.setTextContent(textContent);
        postRepository.save(post);
        return "Post updated successfully with ID: " + id;
    }
    public String deletePost(String id) {
        Post post = findPostById(id);
        postRepository.delete(post);
        return "Post deleted successfully with ID: " + id;
    }

    public String addHashtags(String id, String hashtag) {
        Post post = findPostById(id);
        post.getHashtags().add(hashtag);
        postRepository.save(post);
        return "Hashtag added successfully";
    }
    public String deleteHashtag(String id, String hashtag) {
        Post post = findPostById(id);
        post.getHashtags().remove(hashtag);
        postRepository.save(post);
        return "Hashtag removed successfully";
    }
    public String addComments(String id, Comment comment) {
        Post post = findPostById(id);
        post.addComment(comment);
        postRepository.save(post);
        return "Comment added successfully";
    }
    public String deleteComment(String id, String commentId) {
        Post post = findPostById(id);
        post.getComments().removeIf(comment -> comment.getId().equals(commentId));
        postRepository.save(post);
        return "All comments deleted successfully";
    }
    public String addAttachment(String id, MultipartFile file) throws Exception {
        Post post = findPostById(id);
        postRepository.save(post); //// TODO connect to FileSiste,
    return null;
    }



    private Post findPostById(String id) {
        return postRepository.findById(id)
                .orElseThrow(() ->new PostNotFoundException("Post not found with ID: " + id));
    }
}


