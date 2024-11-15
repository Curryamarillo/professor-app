package com.professor.app.dto.users;

public record UpdatePasswordDTO(String oldPassword,
                                String newPassword) {
}
