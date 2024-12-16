package com.professor.app.services;

import com.professor.app.dto.posts.CommentCreateRequestDTO;
import com.professor.app.dto.posts.CommentDeleteDTO;
import com.professor.app.dto.posts.PostRequestDTO;
import com.professor.app.entities.Comment;
import com.professor.app.entities.Post;
import com.professor.app.exceptions.PostNotFoundException;
import com.professor.app.repositories.PostRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

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
        if (post.getHashtags() == null) {
            post.setHashtags(Set.of(hashtag));
        } else {
            post.getHashtags().add(hashtag);
        }

        postRepository.save(post);
        return "Hashtag added successfully";
    }


    public String deleteHashtag(String id, String hashtag) {
        Post post = findPostById(id);
        post.getHashtags().remove(hashtag);
        postRepository.save(post);
        return "Hashtag removed successfully";
    }


    public String addComments(String id, CommentCreateRequestDTO commentCreateRequestDTO) {
        Post post = findPostById(id);
        Comment comment = Comment.builder()
                .id(UUID.randomUUID().toString())
                .authorId(commentCreateRequestDTO.authorId())
                .content(commentCreateRequestDTO.content())
                .createdAt(LocalDateTime.now())
                .build();

        post.addComment(comment);
        postRepository.save(post);
        return "Comment added successfully";
    }
    public String deleteComment(String id, String commentId) {
        Post post = findPostById(id);
        boolean removed = post.getComments().removeIf(comment -> comment.getId().equals(commentId) );

        if(!removed) {
            throw new IllegalArgumentException("Comment not found for ID: " + commentId);
        }
        postRepository.save(post);
        return "Comments deleted successfully";
    }

    public List<Post> getPostsByHashTag(String hashtag) {
        return postRepository.findByHashtags(hashtag);
    }

    private Post findPostById(String id) {
        return postRepository.findById(id)
                .orElseThrow(() ->new PostNotFoundException("Post not found with ID: " + id));
    }
}


