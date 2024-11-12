package com.professor.app.dto;

public record UserRequestDTO(String name,
                             String surname,
                             String email,
                             String dni,
                             String ROLE,
                             String comments
) {
}
