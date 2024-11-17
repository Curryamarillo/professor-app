package com.professor.app.services;

import com.professor.app.dto.users.ProfessorRequestDTO;
import com.professor.app.dto.users.UserResponseDTO;
import com.professor.app.dto.users.UserUpdateDTO;
import com.professor.app.entities.Professor;
import com.professor.app.entities.User;
import com.professor.app.exceptions.UserAlreadyExistsException;
import com.professor.app.exceptions.UserNotFoundException;
import com.professor.app.mapper.ProfessorMapper;
import com.professor.app.mapper.UserMapper;
import com.professor.app.repositories.UserRepository;
import com.professor.app.roles.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProfessorService {

    @Autowired
    private UserRepository userRepository;

    public UserResponseDTO saveProfessorUser(ProfessorRequestDTO professor) {
        userRepository.findByEmail(professor.email())
                .ifPresent(existingUser -> {
                    throw new UserAlreadyExistsException("User with email " + professor.email() + " already exists");
                });

        Professor professorObject = ProfessorMapper.toProfessor(professor);
        professorObject.setRole(Role.PROFESSOR);
        Professor savedProfessor = userRepository.save(professorObject);
        return UserMapper.toUserResponseDTO(savedProfessor);
    }

    ///  course id, course names, studentsIds
}
