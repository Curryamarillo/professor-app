package com.professor.app.services;

import com.professor.app.dto.users.AdminRequestDTO;
import com.professor.app.dto.users.UserResponseDTO;
import com.professor.app.entities.Admin;
import com.professor.app.exceptions.InvalidUserTypeException;
import com.professor.app.exceptions.UserAlreadyExistsException;
import com.professor.app.exceptions.UserNotFoundException;
import com.professor.app.mapper.AdminMapper;
import com.professor.app.mapper.UserMapper;
import com.professor.app.repositories.UserRepository;
import com.professor.app.roles.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminService {

    @Autowired
    private UserRepository userRepository;


    public UserResponseDTO saveAdminUser(AdminRequestDTO admin) {
        userRepository.findByEmail(admin.email())
                .ifPresent(existingUser -> {
                    throw new UserAlreadyExistsException("User with email " + admin.email() + " already exists");
                });

        Admin adminObject = AdminMapper.toAdmin(admin);
        adminObject.setRole(Role.ADMIN);
        Admin savedAdmin = userRepository.save(adminObject);
        return UserMapper.toUserResponseDTO(savedAdmin);
    }

    public String getComments(String id) {
        return userRepository.findById(id)
                .filter(Admin.class::isInstance)
                .map(Admin.class::cast)
                .map(Admin::getComments)
                .orElseThrow(() -> new UserNotFoundException("User with id: " + id + " does not exist or is not an Admin"));
    }

    public String updateComments(String id, String comments) {
        return userRepository.findById(id)
                .map(user -> {
                    if (user instanceof Admin admin) {
                        admin.setComments(comments);
                        userRepository.save(admin);
                        return "Comments updated successfully for user with id: " + id;
                    }
                    throw new InvalidUserTypeException("User with id: " + id + " is not an Admin");
                })
                .orElseThrow(() -> new UserNotFoundException("User with id: " + id + " not found"));
    }

}


