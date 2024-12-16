package com.professor.app.dto.posts;

import jakarta.validation.constraints.NotNull;

public record CommentCreateRequestDTO(
                                      @NotNull String content,
                                      @NotNull String authorId) {
}
