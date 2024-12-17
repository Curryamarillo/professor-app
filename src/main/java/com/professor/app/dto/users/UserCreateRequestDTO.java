package com.professor.app.dto.users;

public record UserCreateRequestDTO(String name,
                                   String surname,
                                   String email,
                                   String password,
                                   String dni,
                                   String role) {
}
