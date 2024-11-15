package com.professor.app.dto.users;

public record AdminUpdateRequestDTO(
        String name,
        String surname,
        String email,
        String dni
) {
}
