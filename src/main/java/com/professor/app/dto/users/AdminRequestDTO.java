package com.professor.app.dto.users;

import com.professor.app.roles.Role;

public record AdminRequestDTO(String name,
                              String surname,
                              String email,
                              String password,
                              String dni,
                              Role role,
                              String comments) {
}