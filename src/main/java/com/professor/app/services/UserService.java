package com.professor.app.services;

import com.professor.app.dto.users.UserResponseDTO;
import com.professor.app.dto.users.UserUpdateDTO;
import com.professor.app.entities.User;
import com.professor.app.exceptions.UserNotFoundException;
import com.professor.app.mapper.UserMapper;
import com.professor.app.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    // Retrieve all users
    public List<UserResponseDTO> findAllUsers() {
        return userRepository.findAll().stream()
                .map(UserMapper::toUserResponseDTO)
                .collect(Collectors.toList());
    }

    // Retrieve user by ID
    public UserResponseDTO findUserById(String id) {
        User user = getUserById(id);
        return UserMapper.toUserResponseDTO(user);
    }

    // Retrieve users by role
    public List<UserResponseDTO> findUsersByRole(String role) {
        List<User> users = userRepository.findUsersByRole(role);
        if (users.isEmpty()) {
            throw new UserNotFoundException("No users found with role: " + role);
        }
        return users.stream()
                .map(UserMapper::toUserResponseDTO)
                .collect(Collectors.toList());
    }

    // Update user details
    public String updateUser(String id, UserUpdateDTO userUpdateDTO) {
        User user = getUserById(id);

        user.setName(userUpdateDTO.name());
        user.setSurname(userUpdateDTO.surname());
        user.setEmail(userUpdateDTO.email());
        user.setDni(userUpdateDTO.dni());

        userRepository.save(user);
        return "User with ID: " + id + " updated successfully";
    }

    // Update user password
    public String updatePassword(String id, String oldPassword, String newPassword) {
        User user = getUserById(id);

        if (!oldPassword.equals(user.getPassword())) {
            throw new IllegalArgumentException("Invalid credentials");
        }

        user.setPassword(newPassword);
        userRepository.save(user);
        return "Password updated successfully for user with ID: " + id;
    }

    // Delete user by ID
    public String deleteUser(String id) {
        return userRepository.findById(id)
                .map(user -> {
                    userRepository.deleteById(id);
                    return "User with ID: " + id + " deleted successfully";
                })
                .orElseThrow(() -> new UserNotFoundException("User with ID: " + id + " not found"));
    }

    // Private Helper Method
    private User getUserById(String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User with ID: " + id + " not found"));
    }
}

