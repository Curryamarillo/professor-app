package com.professor.app.dto.users;

public record UserCreatedDTO(String id,
                             String name,
                             String surname,
                             String email,
                             String role
                             ) {
}
