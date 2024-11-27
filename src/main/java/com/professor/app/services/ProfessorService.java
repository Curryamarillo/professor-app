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
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
@AllArgsConstructor
public class ProfessorService {

    private final UserRepository userRepository;


    // Save a new Professor
    public UserResponseDTO saveProfessorUser(ProfessorRequestDTO professor) {
        if (userRepository.findByEmail(professor.email()).isPresent()) {
            throw new UserAlreadyExistsException("User with email: " + professor.email() + " already exists");
        }

        Professor professorObject = ProfessorMapper.toProfessor(professor);
        professorObject.setRole(Role.PROFESSOR);
        Professor savedProfessor = userRepository.save(professorObject);
        return UserMapper.toUserResponseDTO(savedProfessor);
    }

    // Get course IDs list
    public Set<String> getCourseIdListById(String id) {
        Professor professor = getProfessorById(id);
        return professor.getCourseIds();
    }

    // Add a course ID
    public String addCourseIdByProfessorId(String id, String courseId) {
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
    // Add student ID
    public String addStudentIdByProfessorId(String id, String studentId) {
        Professor professor = getProfessorById(id);
        professor.getStudentsIds().add(studentId);
        userRepository.save(professor);
        return "Student ID added successfully to user with ID: " + id;
    }
    // Add multiple students ID
    public String addMultipleStudentsId(String id, Set<String> studentIds) {
        Professor professor = getProfessorById(id);
        if (studentIds == null || studentIds.isEmpty()) {
            throw  new IllegalArgumentException("The set of student IDs cannot be null or empty.");
        }
        professor.getStudentsIds().addAll(studentIds);
        userRepository.save(professor);
        return "Student IDs added successfully to user with ID: " + id;
    }

    public String deleteStudentIdByProfessorId(String id, String studentId) {
        Professor professor = getProfessorById(id);
        if (professor.getStudentsIds() == null || !professor.getStudentsIds().contains(studentId)) {
            throw new IllegalArgumentException("Student ID not found for user with ID: " + id);
        }
        professor.getStudentsIds().remove(studentId);
        userRepository.save(professor);
        return "Student successfully deleted to user with ID: " + id;
    }
    /// delete users passing a list
    public String deleteStudentsIdListByProfessorId(String id, Set<String> studentIDListToDelete) {
        Professor professor = getProfessorById(id);

        Set<String> currentStudents = professor.getStudentsIds();
        if (currentStudents == null || currentStudents.isEmpty()) {
            throw new IllegalStateException("No student IDs exist for user with ID: " + id);
        }

        Set<String> existingStudents = new HashSet<>(studentIDListToDelete);
        existingStudents.retainAll(currentStudents);

        if (existingStudents.isEmpty()) {
            throw new IllegalArgumentException("None of the provided student IDs exist for user with ID: " + id);
        }

        Set<String> missingStudents = new HashSet<>(studentIDListToDelete);
        missingStudents.removeAll(currentStudents);

        currentStudents.removeAll(existingStudents);

        userRepository.save(professor);

        if (missingStudents.isEmpty()) {
            return "All student IDs successfully deleted from user with ID: " + id;
        } else {
            return "Some student IDs were not found: " + missingStudents + ". Others were successfully deleted.";
        }
    }

    // Private helper to retrieve a professor by ID
    private Professor getProfessorById(String id) {
        return userRepository.findById(id)
                .filter(Professor.class::isInstance)
                .map(Professor.class::cast)
                .orElseThrow(() -> new UserNotFoundException("User with ID: " + id + " does not exist or is not a Professor"));
    }
}

