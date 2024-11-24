package com.professor.app.services;

import com.professor.app.dto.users.AdminRequestDTO;
import com.professor.app.dto.users.UserResponseDTO;
import com.professor.app.entities.Admin;
import com.professor.app.exceptions.UserAlreadyExistsException;
import com.professor.app.exceptions.UserNotFoundException;
import com.professor.app.mapper.AdminMapper;
import com.professor.app.mapper.UserMapper;
import com.professor.app.repositories.UserRepository;
import com.professor.app.roles.Role;
import org.springframework.stereotype.Service;

@Service
public class AdminService {

    private final UserRepository userRepository;

    public AdminService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Save a new Admin
    public UserResponseDTO saveAdminUser(AdminRequestDTO adminRequestDTO) {
        if (userRepository.findByEmail(adminRequestDTO.email()).isPresent()) {
            throw new UserAlreadyExistsException("User with email " + adminRequestDTO.email() + " already exists");
        }

        Admin admin = AdminMapper.toAdmin(adminRequestDTO);
        admin.setRole(Role.ADMIN);
        Admin savedAdmin = userRepository.save(admin);
        return UserMapper.toUserResponseDTO(savedAdmin);
    }

    // Get comments by admin ID
    public String getCommentsById(String id) {
        Admin admin = getAdminById(id);
        return admin.getComments();
    }

    // Update comments for admin
    public String updateComments(String id, String comments) {
        Admin admin = getAdminById(id);
        admin.setComments(comments);
        userRepository.save(admin);
        return "Comments updated successfully for user with ID: " + id;
    }

    // Private helper to retrieve an Admin by ID
    private Admin getAdminById(String id) {
        return userRepository.findById(id)
                .filter(Admin.class::isInstance)
                .map(Admin.class::cast)
                .orElseThrow(() -> new UserNotFoundException("User with ID: " + id + " does not exist or is not an Admin"));
    }
}



