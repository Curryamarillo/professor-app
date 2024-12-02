package com.professor.app.dto.courses;

import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.UniqueElements;

import java.util.Set;

public record CourseRequestDTO(@NotNull @UniqueElements String code,
                               @NotNull String name,
                               String comments,
                               Set<String> studentListId) {
}
