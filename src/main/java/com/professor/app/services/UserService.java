package com.professor.app.services;

import com.professor.app.dto.users.AdminRequestDTO;
import com.professor.app.dto.users.UserResponseDTO;
import com.professor.app.entities.Admin;
import com.professor.app.entities.User;
import com.professor.app.exceptions.UserAlreadyExistsException;
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
                .map(UserMapper::userResponseDTO)
                .collect(Collectors.toList());
    }

    public Optional<UserResponseDTO> findUserById(String id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty()) {
            throw new UserNotFoundException("User with id: " + id + " not found");
        }
        UserResponseDTO userResponseDTO = UserMapper.userResponseDTO(user.get());
        return Optional.of(userResponseDTO);

    }
    public List<UserResponseDTO> findUsersByRole(String role) {
        List<User> userList = userRepository.findUsersByRole(role);
        if (userList.isEmpty()) {
            throw new UserNotFoundException("User with role: " + role + "not found");
        }
        return userList.stream()
                .map(UserMapper::userResponseDTO)
                .collect(Collectors.toList());
    }

    public UserResponseDTO saveAdminUser(AdminRequestDTO admin) {
        Optional<User> existingAdmin = userRepository.findByEmail(admin.email());
        if (existingAdmin.isPresent()) {
            throw new UserAlreadyExistsException("User with email " + admin.email() + " already exists");
        }
        Admin adminObject = new Admin();
        adminObject.setName(admin.name());
        adminObject.setSurname(admin.surname());
        adminObject.setEmail(admin.email());
        adminObject.setPassword(admin.password());
        adminObject.setDni(admin.dni());
        adminObject.setRole(admin.role());
        adminObject.setComments(admin.comments());

        Admin savedAdmin = userRepository.save(adminObject);

        return UserMapper.userResponseDTO(savedAdmin);
    }


}

