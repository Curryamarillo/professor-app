package com.professor.app.services;

import com.professor.app.dto.users.StudentRequestDTO;
import com.professor.app.dto.users.UserResponseDTO;
import com.professor.app.entities.Student;
import com.professor.app.exceptions.UserAlreadyExistsException;
import com.professor.app.exceptions.UserNotFoundException;
import com.professor.app.mapper.StudentMapper;
import com.professor.app.mapper.UserMapper;
import com.professor.app.repositories.UserRepository;
import com.professor.app.roles.Role;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
@AllArgsConstructor
public class StudentService {

    private final UserRepository userRepository;

    public UserResponseDTO saveStudentUser(StudentRequestDTO studentRequestDTO) {
        if (userRepository.findByEmail(studentRequestDTO.email()).isPresent()) {
            throw new UserAlreadyExistsException("User with email: " + studentRequestDTO.email() + " already exists.");
        }
        Student studentObject = StudentMapper.toStudent(studentRequestDTO);
        studentObject.setRole(Role.STUDENT);
        Student savedStudent = userRepository.save(studentObject);
            return UserMapper.toUserResponseDTO(savedStudent);
    }
    /// methods to manage enrolled courses IDs
    public Set<String> getEnrolledCourseIDByStudentID(String id) {
        Student student = getStudentById(id);
        return student.getEnrolledCoursesId();
    }
    public String addEnrolledCourseIDByStudentID(String id, String courseId) {
        Student student = getStudentById(id);
        student.getEnrolledCoursesId().add(courseId);
        return "Course ID added successfully to user with ID: " + id;
    }
    public String removeEnrolledCourseIDByStudentID(String id, String enrolledCourseId) {
        Student student = getStudentById(id);
        Set<String> courseIdList = Optional.ofNullable(student.getEnrolledCoursesId())
                .orElseThrow(() -> new IllegalStateException("Course IDs not initialized for user with ID: " + id));
        if (!courseIdList.remove(enrolledCourseId)) {
            throw new IllegalArgumentException("Course with ID: " + enrolledCourseId + " not found");
        }
        userRepository.save(student);
        return "Course ID removed successfully from user with ID: " + id;
    }

    /// Helper to get student by ID
    private Student getStudentById(String id) {
        return userRepository.findById(id)
                .filter(Student.class::isInstance)
                .map(Student.class::cast)
                .orElseThrow(() -> new UserNotFoundException("User with ID: " + id + " does not exist or is not a Student"));
    }
}
