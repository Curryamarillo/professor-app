package com.professor.app.dto.posts;

import com.professor.app.roles.Role;
import jakarta.validation.constraints.NotNull;

import java.util.Set;

public record PostRequestDTO(@NotNull Set<String> postAuthor,
                             @NotNull String title,
                             @NotNull Role postedByRole,
                             @NotNull String textContent) {
}
