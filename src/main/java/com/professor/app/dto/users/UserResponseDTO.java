package com.professor.app.dto.users;

import com.professor.app.roles.Role;

public record UserResponseDTO(String id,
                              String name,
                              String surname,
                              String email,
                              String dni,
                              Role role) {
}
