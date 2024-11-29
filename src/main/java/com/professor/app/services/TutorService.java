package com.professor.app.services;

import com.professor.app.dto.users.TutorRequestDTO;
import com.professor.app.dto.users.UserResponseDTO;
import com.professor.app.entities.Tutor;
import com.professor.app.exceptions.UserAlreadyExistsException;
import com.professor.app.exceptions.UserNotFoundException;
import com.professor.app.mapper.TutorMapper;
import com.professor.app.mapper.UserMapper;
import com.professor.app.repositories.UserRepository;
import com.professor.app.roles.Role;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@AllArgsConstructor
public class TutorService {

    private final UserRepository userRepository;

    // Save a new tutor
    public UserResponseDTO saveTutorUser(TutorRequestDTO tutorRequestDTO) {
        if (userRepository.findByEmail(tutorRequestDTO.email()).isPresent()) {
            throw new UserAlreadyExistsException("User with email: " + tutorRequestDTO.email() + " already exists");
        }
        Tutor tutor = TutorMapper.toTutor(tutorRequestDTO);
        tutor.setRole(Role.TUTOR);
        Tutor savedTutor = userRepository.save(tutor);
        return UserMapper.toUserResponseDTO(savedTutor);
    }

    // Get all student ID
    public Set<String> getStudentsIDsByTutorId(String id) {
        Tutor tutor = getTutorById(id);
        return tutor.getTutoredStudentsId();
    }

    // Add student ID
    public String addStudentIDByTutorID(String id, String studentId) {
        Tutor tutor = getTutorById(id);

        tutor.getTutoredStudentsId().add(studentId);
        userRepository.save(tutor);
        return "Student ID added successfully to user with ID: " + id;
    }

    // Add a list of student ID
    public String addMultipleStudentsIDByTutorID(String id, List<String> studentIDs) {
        Tutor tutor = getTutorById(id);
        if (studentIDs == null || studentIDs.isEmpty()) {
            throw new IllegalArgumentException("The list of student IDs cannot be null or empty.");
        }
        tutor.getTutoredStudentsId().addAll(studentIDs);
        userRepository.save(tutor);
        return "Student IDs added successfully to user with ID: " + id;
    }

    // Delete student ID by Tutor ID
    public String deleteStudentIDByTutorId(String id, String studentId) {
        Tutor tutor = getTutorById(id);
        if (tutor.getTutoredStudentsId() == null || !tutor.getTutoredStudentsId().contains(studentId)) {
            throw new IllegalArgumentException("Student ID not found for user with ID: " + id);
        }
        tutor.getTutoredStudentsId().remove(studentId);
        userRepository.save(tutor);
        return "Student successfully removed to user with ID: " + id;
    }

    // Delete multiple student IDs by Tutor ID
    public String deleteStudentsIDListByTutorID(String id, Set<String> studentIdList) {
        Tutor tutor = getTutorById(id);
        Set<String> currentStudents = tutor.getTutoredStudentsId();

        if (currentStudents == null || currentStudents.isEmpty()) {
            throw new IllegalStateException("No student IDs exist for user with ID: " + id);
        }
        Set<String> mutableStudentIdList = new HashSet<>(studentIdList);
        mutableStudentIdList.retainAll(currentStudents);

        if (mutableStudentIdList.isEmpty()) {
            throw new IllegalArgumentException("None of the provided student IDs exist for user with ID: " + id);
        }

        currentStudents.removeAll(mutableStudentIdList);
        userRepository.save(tutor);

        return "Student IDs successfully deleted from user with ID: " + id;
    }


    /// helper to get tutor by ID
    private Tutor getTutorById(String id) {
        return userRepository.findById(id).filter(Tutor.class::isInstance).map(Tutor.class::cast).orElseThrow(() -> new UserNotFoundException("User with ID: " + id + " not found or is not a Tutor"));
    }
}
