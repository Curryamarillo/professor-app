package com.professor.app.services;

import com.professor.app.dto.users.AdminRequestDTO;
import com.professor.app.dto.users.AdminUpdateRequestDTO;
import com.professor.app.dto.users.UserResponseDTO;
import com.professor.app.entities.Admin;
import com.professor.app.entities.User;
import com.professor.app.exceptions.InvalidUserTypeException;
import com.professor.app.exceptions.UserAlreadyExistsException;
import com.professor.app.exceptions.UserNotFoundException;
import com.professor.app.mapper.AdminMapper;
import com.professor.app.mapper.UserMapper;
import com.professor.app.repositories.UserRepository;
import com.professor.app.roles.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AdminService {

    @Autowired
    private UserRepository userRepository;


    public UserResponseDTO saveAdminUser(AdminRequestDTO admin) {
        Optional<User> existingAdmin = userRepository.findByEmail(admin.email());
        if (existingAdmin.isPresent()) {
            throw new UserAlreadyExistsException("User with email " + admin.email() + " already exists");
        }
        Admin adminObject = AdminMapper.toAdmin(admin);
        adminObject.setRole(Role.ADMIN);
        Admin savedAdmin = userRepository.save(adminObject);
        return UserMapper.toUserResponseDTO(savedAdmin);
    }

    public String updateAdminUser(String id, AdminUpdateRequestDTO admin) {
        Optional<User> existUser = userRepository.findById(id);
        if (existUser.isEmpty()){
            throw new UserNotFoundException("User with email: " + admin.email() + " not exists");
        } else {
            User user = existUser.get();

            user.setName(admin.name());
            user.setSurname(admin.surname());
            user.setEmail(admin.email());
            user.setDni(admin.dni());

            userRepository.save(user);
        }
        return "User with id: " + id + " updated successfully";
    }
    public String updateComments(String id, String comments) {
        Optional<User> existsUser = userRepository.findById(id);
        if (existsUser.isEmpty()) {
            throw new UserNotFoundException("User with id: " + id + " not found");
        }

        User user = existsUser.get();

        if (user instanceof Admin admin) {
            admin.setComments(comments);
            userRepository.save(admin);
            return "Comments updated successfully for user with id: " + id;
        } else {
            throw new InvalidUserTypeException("User with id: " + id + " is not an Admin");
        }
    }
}
