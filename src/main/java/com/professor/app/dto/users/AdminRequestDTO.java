package com.professor.app.dto.users;

public record AdminRequestDTO(String name,
                              String surname,
                              String email,
                              String password,
                              String dni,
                              String comments) {
}