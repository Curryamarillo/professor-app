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
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@AllArgsConstructor
public class AssistantService {

    @Autowired
    private UserRepository userRepository;

 /// Assistant
    public UserResponseDTO saveAssistant(AssistantRequestDTO assistant) {
        userRepository.findByEmail(assistant.email())
                .ifPresent(existing -> {
                    throw new UserAlreadyExistsException("User with email: " + assistant.email() + " already exists");
                });

        Assistant assistantObject = AssistantMapper.toAssistant(assistant);
        assistantObject.setRole(Role.ASSISTANT);

        Assistant savedAssistant = userRepository.save(assistantObject);
        return UserMapper.toUserResponseDTO(savedAssistant);
    }

 /// Duties

    public Set<String> getDutiesById(String id) {
        return userRepository.findById(id)
                .filter(Assistant.class::isInstance)
                .map(Assistant.class::cast)
                .map(Assistant::getDuties)
                .orElseThrow(() -> new UserNotFoundException("User with id: " + id + " does not exists or is not an Assistant"));
    }

    public String addDutyById(String id, String duty) {
        Assistant assistant = userRepository.findById(id)
                .filter(Assistant.class::isInstance)
                .map(Assistant.class::cast)
                .orElseThrow(() -> new UserNotFoundException("User with id: " + id + " does not exists or is not an Assistant"));
       Set<String> currentDuties = assistant.getDuties();
       currentDuties.add(duty);
        userRepository.save(assistant);
        return "Duty added successfully to id: " + id;
    }
    public String removeDuty(String id, String duty) {
        Assistant assistant = userRepository.findById(id)
                .filter(Assistant.class::isInstance)
                .map(Assistant.class::cast)
                .orElseThrow(() -> new UserNotFoundException("User with id: " + id + " does not exist or is not an Assistant"));

        Set<String> duties = Optional.ofNullable(assistant.getDuties())
                .orElseThrow(() -> new IllegalStateException("Duties not initialized for user with id: " + id));

        if (!duties.remove(duty)) {
            throw new IllegalArgumentException("Duty '" + duty + "' not found for user with id: " + id);
        }

        userRepository.save(assistant);
        return "Duty removed successfully from user with id: " + id;
    }



    /// Courses

    public List<String> getCoursesById(String id) {
        return userRepository.findById(id)
                .filter(Assistant.class::isInstance)
                .map(Assistant.class::cast)
                .map(Assistant::getCourseId)
                .orElseThrow(() -> new UserNotFoundException("User with id: " + id + " does not exists or is not an Assistant"));
    }

    public String updateCourseId(String id, String courseId) {
        Assistant assistant = userRepository.findById(id)
                .filter(Assistant.class::isInstance)
                .map(Assistant.class::cast)
                .orElseThrow(() -> new UserNotFoundException("User with id: " + id + " does not exist or is not an Assistant"));
        assistant.setCourseId(Collections.singletonList(courseId));
        userRepository.save(assistant);
        return "Course added successfully to id: " + id;
    }
    public String addCourseId(String id, String courseId) {
        Assistant assistant = userRepository.findById(id)
                .filter(Assistant.class::isInstance)
                .map(Assistant.class::cast)
                .orElseThrow(() -> new UserNotFoundException("User with id: " + id + " does not exists or is not an Assistant"));
        assistant.setCourseId(List.of(courseId));
        userRepository.save(assistant);
        return "Course added successfully to id: " + id;
    }
    public String removeCourseId(String id, String courseId) {
        Assistant assistant = userRepository.findById(id)
                .filter(Assistant.class::isInstance)
                .map(Assistant.class::cast)
                .orElseThrow(() -> new UserNotFoundException("User with id: " + id + " does not exists or is not an Assistant"));
        if (!assistant.getCourseId().contains(courseId)) {
            throw new UserNotFoundException("Course with id: " + courseId + " does not exists for user id: " + id);
        }
        assistant.setCourseId(List.of(""));
        userRepository.save(assistant);
        return "Course removed successfully to user id: " + id;
    }
}

