package com.professor.app.services;

import com.professor.app.dto.users.UserResponseDTO;
import com.professor.app.entities.User;
import com.professor.app.exceptions.UserNotFoundException;
import com.professor.app.mapper.UserMapper;
import com.professor.app.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public List<UserResponseDTO> findAllUsers() {
        List<User> userList = userRepository.findAll();
        return userList.stream()
                .map(UserMapper::toUserResponseDTO)
                .collect(Collectors.toList());
    }

    public Optional<UserResponseDTO> findUserById(String id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty()) {
            throw new UserNotFoundException("User with id: " + id + " not found");
        }
        UserResponseDTO userResponseDTO = UserMapper.toUserResponseDTO(user.get());
        return Optional.of(userResponseDTO);

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

    public String updatePassword(String id, String oldPassword, String newPassword) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (!oldPassword.equals(user.getPassword())) {
            throw  new IllegalArgumentException("Invalid credentials");
        }
        user.setPassword(newPassword);
        userRepository.save(user);
        return "Password updated successfully";
    }

    public String deleteUser(String id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty()) {
            throw  new UserNotFoundException("User with id: " + id + " not found");
        }
        userRepository.deleteById(id);
        return "User with id: " + id + " has been deleted successfully";
    }


}

