package com.professor.app.services;

import com.professor.app.dto.users.AssistantRequestDTO;
import com.professor.app.dto.users.UserResponseDTO;
import com.professor.app.entities.Assistant;
import com.professor.app.exceptions.UserAlreadyExistsException;
import com.professor.app.exceptions.UserNotFoundException;
import com.professor.app.mapper.AssistantMapper;
import com.professor.app.mapper.UserMapper;
import com.professor.app.repositories.UserRepository;
import com.professor.app.roles.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AssistantService {

    private final UserRepository userRepository;

    // Save a new Assistant
    public UserResponseDTO saveAssistant(AssistantRequestDTO assistantRequestDTO) {
        if (userRepository.findByEmail(assistantRequestDTO.email()).isPresent()) {
            throw new UserAlreadyExistsException("User with email: " + assistantRequestDTO.email() + " already exists");
        }

        Assistant assistant = AssistantMapper.toAssistant(assistantRequestDTO);
        assistant.setRole(Role.ASSISTANT);

        Assistant savedAssistant = userRepository.save(assistant);
        return UserMapper.toUserResponseDTO(savedAssistant);
    }

    // Duties Management
    public Set<String> getDutiesById(String id) {
        return getAssistantById(id).getDuties();
    }

    public String addDutyById(String id, String duty) {
        Assistant assistant = getAssistantById(id);
        assistant.getDuties().add(duty);
        userRepository.save(assistant);
        return "Duty added successfully to user with ID: " + id;
    }

    public String removeDuty(String id, String duty) {
        Assistant assistant = getAssistantById(id);
        Set<String> duties = Optional.ofNullable(assistant.getDuties())
                .orElseThrow(() -> new IllegalStateException("Duties not initialized for user with ID: " + id));

        if (!duties.remove(duty)) {
            throw new IllegalArgumentException("Duty '" + duty + "' not found for user with ID: " + id);
        }

        userRepository.save(assistant);
        return "Duty removed successfully from user with ID: " + id;
    }

    // Courses Management
    public List<String> getCoursesById(String id) {
        return getAssistantById(id).getCourseId();
    }

    public String addCourseId(String id, String courseId) {
        Assistant assistant = getAssistantById(id);
        List<String> courseIds = assistant.getCourseId();
        if (courseIds == null || courseIds.isEmpty()) {
            assistant.setCourseId(Collections.singletonList(courseId));
        } else {
            courseIds.add(courseId);
        }
        userRepository.save(assistant);
        return "Course added successfully to user with ID: " + id;
    }

    public String removeCourseId(String id, String courseId) {
        Assistant assistant = getAssistantById(id);
        List<String> courseIds = Optional.ofNullable(assistant.getCourseId())
                .orElseThrow(() -> new IllegalStateException("Courses not initialized for user with ID: " + id));

        if (!courseIds.remove(courseId)) {
            throw new IllegalArgumentException("Course with ID: " + courseId + " does not exist for user with ID: " + id);
        }

        userRepository.save(assistant);
        return "Course removed successfully from user with ID: " + id;
    }

    // Private Helper
    private Assistant getAssistantById(String id) {
        return userRepository.findById(id)
                .filter(Assistant.class::isInstance)
                .map(Assistant.class::cast)
                .orElseThrow(() -> new UserNotFoundException("User with ID: " + id + " does not exist or is not an Assistant"));
    }
}
