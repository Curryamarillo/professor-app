package com.professor.app.dto;

import com.professor.app.roles.Role;

public record AdminRequestDTO(String name,
                              String surname,
                              String email,
                              String dni,
                              Role role,
                              String comments) {
}