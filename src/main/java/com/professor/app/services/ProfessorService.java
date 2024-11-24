package com.professor.app.services;

import com.professor.app.dto.users.ProfessorRequestDTO;
import com.professor.app.dto.users.UserResponseDTO;
import com.professor.app.entities.Professor;
import com.professor.app.exceptions.UserAlreadyExistsException;
import com.professor.app.exceptions.UserNotFoundException;
import com.professor.app.mapper.ProfessorMapper;
import com.professor.app.mapper.UserMapper;
import com.professor.app.repositories.UserRepository;
import com.professor.app.roles.Role;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
public class ProfessorService {

    private final UserRepository userRepository;

    public ProfessorService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Save a new Professor
    public UserResponseDTO saveProfessorUser(ProfessorRequestDTO professor) {
        if (userRepository.findByEmail(professor.email()).isPresent()) {
            throw new UserAlreadyExistsException("User with email " + professor.email() + " already exists");
        }

        Professor professorObject = ProfessorMapper.toProfessor(professor);
        professorObject.setRole(Role.PROFESSOR);
        Professor savedProfessor = userRepository.save(professorObject);
        return UserMapper.toUserResponseDTO(savedProfessor);
    }

    // Retrieve course IDs
    public Set<String> getCourseIdListById(String id) {
        Professor professor = getProfessorById(id);
        return professor.getCourseIds();
    }

    // Add a course ID
    public String addCourseIdById(String id, String courseId) {
        Professor professor = getProfessorById(id);
        professor.getCourseIds().add(courseId);
        userRepository.save(professor);
        return "Course ID added successfully to user with ID: " + id;
    }

    // Remove a course ID
    public String removeCourseIdById(String id, String courseId) {
        Professor professor = getProfessorById(id);
        Set<String> courseIdList = Optional.ofNullable(professor.getCourseIds())
                .orElseThrow(() -> new IllegalStateException("Course IDs not initialized for user with ID: " + id));

        if (!courseIdList.remove(courseId)) {
            throw new IllegalArgumentException("Course with ID: " + courseId + " not found");
        }

        userRepository.save(professor);
        return "Course ID removed successfully from user with ID: " + id;
    }

    // Retrieve student IDs
    public Set<String> getStudentIdList(String id) {
        Professor professor = getProfessorById(id);
        return professor.getStudentsIds();
    }

    // Private helper to retrieve a professor by ID
    private Professor getProfessorById(String id) {
        return userRepository.findById(id)
                .filter(Professor.class::isInstance)
                .map(Professor.class::cast)
                .orElseThrow(() -> new UserNotFoundException("User with ID: " + id + " does not exist or is not a Professor"));
    }
}

