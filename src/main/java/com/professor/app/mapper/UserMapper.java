package com.professor.app.mapper;

import com.professor.app.dto.users.UserResponseDTO;
import com.professor.app.entities.User;

public class UserMapper {

    public static UserResponseDTO userResponseDTO(User user) {
        return new UserResponseDTO(
                user.getId(),
                user.getName(),
                user.getSurname(),
                user.getEmail(),
                user.getDni(),
                user.getRole()
        );
    }
}
