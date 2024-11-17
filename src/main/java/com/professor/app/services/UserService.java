package com.professor.app.services;

import com.professor.app.dto.users.UserResponseDTO;
import com.professor.app.dto.users.UserUpdateDTO;
import com.professor.app.entities.User;
import com.professor.app.exceptions.UserNotFoundException;
import com.professor.app.mapper.UserMapper;
import com.professor.app.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public List<UserResponseDTO> findAllUsers() {
        return userRepository.findAll().stream()
                .map(UserMapper::toUserResponseDTO)
                .collect(Collectors.toList());
    }

    public UserResponseDTO findUserById(String id) {
        return userRepository.findById(id)
                .map(UserMapper::toUserResponseDTO)
                .orElseThrow(() -> new UserNotFoundException("User with id: " + id + " not found"));
    }

    public List<UserResponseDTO> findUsersByRole(String role) {
        List<User> userList = userRepository.findUsersByRole(role);
        if (userList.isEmpty()) {
            throw new UserNotFoundException("User with role: " + role + " not found");
        }

                return userList.stream()
                .map(UserMapper::toUserResponseDTO)
                .collect(Collectors.toList());
    }

    public String updateUser(String id, UserUpdateDTO userUpdateDTO) {
        return userRepository.findById(id)
                .map(user -> {
                    user.setName(userUpdateDTO.name());
                    user.setSurname(userUpdateDTO.surname());
                    user.setEmail(userUpdateDTO.email());
                    user.setDni(userUpdateDTO.dni());
                    userRepository.save(user);
                    return "User with id: " + id + " updated successfully";
                })
                .orElseThrow(() -> new UserNotFoundException("User with email: " + userUpdateDTO.email() + " not exists"));
    }

    public String updatePassword(String id, String oldPassword, String newPassword) {
        return userRepository.findById(id)
                .map(user -> {
                    if (!oldPassword.equals(user.getPassword())) {
                        throw new IllegalArgumentException("Invalid credentials");
                    }
                    user.setPassword(newPassword);
                    userRepository.save(user);
                    return "Password updated successfully";
                })
                .orElseThrow(() -> new UserNotFoundException("User with id: " + id + " not found"));
    }

    public String deleteUser(String id) {
        return userRepository.findById(id)
                .map(user -> {
                    userRepository.deleteById(id);
                    return "User with id: " + id + " has been deleted successfully";
                })
                .orElseThrow(() -> new UserNotFoundException("User with id: " + id + " not found"));
    }


}

