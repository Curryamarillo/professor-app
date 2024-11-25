package com.professor.app.services;

import com.professor.app.dto.users.AssistantRequestDTO;
import com.professor.app.dto.users.UserResponseDTO;
import com.professor.app.entities.Assistant;
import com.professor.app.entities.User;
import com.professor.app.exceptions.UserAlreadyExistsException;
import com.professor.app.exceptions.UserNotFoundException;
import com.professor.app.mapper.AssistantMapper;
import com.professor.app.mapper.UserMapper;
import com.professor.app.repositories.UserRepository;
import com.professor.app.roles.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;


import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AssistantServiceTests {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AssistantService assistantService;

    UserResponseDTO userResponseDTO1;

    AssistantRequestDTO assistantRequestDTO1;


    Assistant assistantUser1;
    Assistant assistantUser2;

    String courseId1;
    String courseId2;

    String duty1;
    String duty2;


    @BeforeEach
    void setUp() {
        courseId1 = "course1";
        courseId2 = "course2";

        duty1 = "duty 1";
        duty2 = "duty 2";
        assistantUser1 = Assistant.builder()
                .id("10000")
                .name("Leonel")
                .surname("Messi")
                .email("campeon10@gmail.com")
                .dni("10000")
                .role(Role.ASSISTANT)
                .password("password1")
                .createdAt(LocalDateTime.of(2024, 1, 1, 1, 0, 0))
                .modifiedAt(LocalDateTime.of(2024, 1, 1, 1, 1, 10))
                .duties(new HashSet<>(Set.of(duty1, duty2)))
                .courseId(new ArrayList<>(List.of(courseId1, courseId2)))
                .build();

        assistantUser2 = Assistant.builder()
                .id("10001")
                .name("Fideo")
                .surname("Dimaria")
                .email("campeon11@gmail.com")
                .dni("10002")
                .role(Role.ADMIN)
                .password("password2")
                .createdAt(LocalDateTime.of(2024, 1, 1, 1, 0, 0))
                .modifiedAt(LocalDateTime.of(2024, 1, 1, 1, 1, 10))
                .duties(new HashSet<>(Set.of(duty1, duty2)))
                .courseId(new ArrayList<>(List.of(courseId1, courseId2)))
                .build();

        assistantRequestDTO1 = new AssistantRequestDTO("Leonel", "Messi", "campeon10@gmail.com", "password1", "10000", List.of(courseId1, courseId2), Set.of(duty1, duty2));

        userResponseDTO1 = new UserResponseDTO("10000", "Leonel", "Messi", "campeon10@gmail.com", "10000", Role.ASSISTANT);


    }

    @Test
    @DisplayName("Save assistant user successfully")
    public void saveAssistantUserSuccessfully() {
        String email = "campeon10@gmail.com";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(userRepository.save(assistantUser1)).thenReturn(assistantUser1);

        try (MockedStatic<AssistantMapper> assistantMapperMockedStatic = Mockito.mockStatic(AssistantMapper.class);
             MockedStatic<UserMapper> userMapperMockedStatic = Mockito.mockStatic(UserMapper.class)) {

            assistantMapperMockedStatic.when(() -> AssistantMapper.toAssistant(assistantRequestDTO1)).thenReturn(assistantUser1);
            userMapperMockedStatic.when(() -> UserMapper.toUserResponseDTO(assistantUser1)).thenReturn(userResponseDTO1);

            UserResponseDTO result = assistantService.saveAssistant(assistantRequestDTO1);

            assertNotNull(result);
            assertEquals("10000", result.id());
            assertEquals("Leonel", result.name());
            assertEquals("Messi", result.surname());
            assertEquals("campeon10@gmail.com", result.email());
            assertEquals(Role.ASSISTANT, result.role());
        }
    }

    @Test
    @DisplayName("Throws UserAlreadyExistsException when save")
    public void saveAssistantUserThrowsException() {
        String email = "campeon10@gmail.com";

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(assistantUser1));

        UserAlreadyExistsException exception = assertThrows(UserAlreadyExistsException.class, () -> assistantService.saveAssistant(assistantRequestDTO1));

        assertEquals("User with email: " + email + " already exists", exception.getMessage());

    }

    @Test
    @DisplayName("Update duties successfully")
    public void addDutiesByIdNotRepeatSuccessfully() {
        String id = "10000";
        String newDuty = "New duty";

        when(userRepository.findById(id)).thenReturn(Optional.of(assistantUser1));
        when(userRepository.save(assistantUser1)).thenReturn(assistantUser1);
        String result = assistantService.addDutyById(id, newDuty);

        assertEquals("Duty added successfully to user with ID: " + id, result);
        assertEquals(Set.of(duty1, duty2, newDuty), assistantUser1.getDuties());
        assertTrue(assistantUser1.getDuties().contains(newDuty));
        verify(userRepository).findById(id);
        verify(userRepository).save(assistantUser1);

    }

    @Test
    @DisplayName("Throws UserNotFoundException when user is not found")
    public void addDutiesByIdNotRepeatThrowsUserNotFoundException() {
        String id = "20002";
        String newDuty = "New duty";

        when(userRepository.findById(id)).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> assistantService.addDutyById(id, newDuty));

        assertEquals("User with ID: " + id + " does not exist or is not an Assistant", exception.getMessage());
        verify(userRepository).findById(id);
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Update duty instance ASSISTANT error")
    public void updateDutyIdInstanceException() {
        String id = "10000";
        String newDuty = "course id";
        User user = new User();
        user.setId(id);

        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> assistantService.addDutyById(id, newDuty)
        );
        assertEquals("User with ID: " + id + " does not exist or is not an Assistant", exception.getMessage());
        verify(userRepository).findById(id);
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Get course by id successfully")
    public void getCourseById() {
        String id = "10000";
        when(userRepository.findById(id)).thenReturn(Optional.of(assistantUser1));

        List<String> result = assistantService.getCoursesById(id);

        assertEquals(2, result.size());
        assertEquals("course1", result.getFirst());
        assertEquals("course2", result.get(1));
        verify(userRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("Get course by id throws User Not Found Exception")
    public void getCourseByIdUserNotFound() {
        String id = "";
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> assistantService.getCoursesById(id));
        assertEquals("User with ID: " + id + " does not exist or is not an Assistant", exception.getMessage());
        verify(userRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("Get duties by id successfully")
    public void getDutiesByIdSuccessfully() {
        String id = "10000";
        when(userRepository.findById(id)).thenReturn(Optional.of(assistantUser1));

        Set<String> result = assistantService.getDutiesById(id);

        assertEquals(2, result.size());
        assertTrue(result.contains("duty 1"));
        assertTrue(result.contains("duty 2"));
        assertEquals(assistantUser1.getDuties(), result);
        verify(userRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("Get duties throws User Not Found Exception")
    public void getDutiesByIdThrowsUserNotFoundException() {
        String id = "30000";
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> assistantService.getDutiesById(id));
        assertEquals("User with ID: " + id + " does not exist or is not an Assistant", exception.getMessage());
        verify(userRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("Add duty successfully")
    public void addDutyByIdSuccessfully() {
        String id = "10000";
        String duty = "New duty";

        when(userRepository.findById(id)).thenReturn(Optional.of(assistantUser1));
        when(userRepository.save(assistantUser1)).thenReturn(assistantUser1);

        String result = assistantService.addDutyById(id, duty);

        assertEquals("Duty added successfully to user with ID: " + id, result);
        ;
        assertTrue(assistantUser1.getDuties().contains(duty));
    }

    @Test
    @DisplayName("Add duty throws exception")
    void addDutyByIdUserNotFound() {
        String id = "123";
        String duty = "New duty";

        when(userRepository.findById(id)).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> assistantService.addDutyById(id, duty));

        assertEquals("User with ID: " + id + " does not exist or is not an Assistant", exception.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Remove duty successfully")
    public void removeDutySuccessfully() {
        String id = "10000";
        String dutyToRemove = "duty 1";

        when(userRepository.findById(id)).thenReturn(Optional.of(assistantUser1));
        when(userRepository.save(assistantUser1)).thenReturn(assistantUser1);

        String result = assistantService.removeDuty(id, dutyToRemove);

        assertEquals("Duty removed successfully from user with ID: " + id, result);
        assertFalse(assistantUser1.getDuties().contains(dutyToRemove));
        verify(userRepository).findById(id);
        verify(userRepository).save(assistantUser1);
    }

    @Test
    @DisplayName("Duty not found throws Illegal argument exception")
    public void removeDutyThrowsIllegalArgumentException() {
        String id = "1000";
        String dutyToRemove = "non existing duty";

        when(userRepository.findById(id)).thenReturn(Optional.of(assistantUser1));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> assistantService.removeDuty(id, dutyToRemove)
        );
        assertEquals("Duty '" + dutyToRemove + "' not found for user with ID: " + id, exception.getMessage());
        verify(userRepository).findById(id);
        verify(userRepository, never()).save(any());
    }
    @Test
    @DisplayName("Remove duty throws user not found exception")
    public void removeDutyThrowsUserNotFoundException() {
        String id = "10000";
        String dutyToRemove = "duty 1";

        when(userRepository.findById(id)).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> assistantService.removeDuty(id, dutyToRemove)
        );

        assertEquals("User with ID: " + id + " does not exist or is not an Assistant", exception.getMessage());
        verify(userRepository).findById(id);
        verify(userRepository, never()).save(any());
    }
    @Test
    @DisplayName("Add course Id successfully")
    public void addCourseIdSuccessfully() {
        String id = "10000";
        String courseId = "courseId1";

        when(userRepository.findById(id)).thenReturn(Optional.of(assistantUser1));
        when(userRepository.save(assistantUser1)).thenReturn(assistantUser1);

        String result = assistantService.addCourseId(id, courseId);

        assertEquals("Course added successfully to user with ID: " + id, result);
        assertTrue(assistantUser1.getCourseId().contains(courseId));
        verify(userRepository).findById(id);
        verify(userRepository).save(assistantUser1);
    }
    @Test
    @DisplayName("Add course ID with no previously course ID list")
    void addCourseWithNoCourseIdList() {
        String assistantId = "40000";
        String courseId = "course1";

        Assistant assistant = new Assistant();
        assistant.setId(assistantId);
        assistant.setCourseId(null);

        when(userRepository.findById(assistantId)).thenReturn(Optional.of(assistant));
        when(userRepository.save(any(Assistant.class))).thenAnswer(invocation -> invocation.getArgument(0));

        String result = assistantService.addCourseId(assistantId, courseId);

        assertNotNull(result);
        assertEquals("Course added successfully to user with ID: " + assistantId, result);
        assertNotNull(assistant.getCourseId());
        assertEquals(1, assistant.getCourseId().size());
        assertTrue(assistant.getCourseId().contains(courseId));
        verify(userRepository, times(1)).save(assistant);
    }
    @Test
    @DisplayName("Add course throws user not found exception")
    public void addCourseIdThrowsUserNotFoundException() {
        String id = "50000";
        String courseId = "course123";

        when(userRepository.findById(id)).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> assistantService.addCourseId(id, courseId)
        );

        assertEquals("User with ID: " + id + " does not exist or is not an Assistant", exception.getMessage());
        verify(userRepository).findById(id);
        verify(userRepository, never()).save(any());
    }
    @Test
    @DisplayName("Add course throws user not assistant exception")
    public void addCourseIdThrowsUserNotAssistantException() {
        String id = "90000";
        String courseId = "course123";

        User user = new User();
        user.setId(id);

        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> assistantService.addCourseId(id, courseId)
        );

        assertEquals("User with ID: " + id + " does not exist or is not an Assistant", exception.getMessage());
        verify(userRepository).findById(id);
        verify(userRepository, never()).save(any());
    }
    @Test
    @DisplayName("Remove course id successfully")
    public void removeCourseIdSuccessfully() {
        String id = "10000";
        String courseId = "course1";

        when(userRepository.findById(id)).thenReturn(Optional.of(assistantUser1));
        when(userRepository.save(assistantUser1)).thenReturn(assistantUser1);

        String result = assistantService.removeCourseId(id, courseId);

        assertEquals("Course removed successfully from user with ID: " + id, result);
        assertFalse(assistantUser1.getCourseId().contains(courseId));
        verify(userRepository).findById(id);
        verify(userRepository).save(assistantUser1);
    }

    @Test
    @DisplayName("Remove non existing course user throws illegal argument exception")
    public void removeNonExistingCourseIdThrowsIllegalArgumentException() {
        String id = "10000";
        String courseId = "course3";

        when(userRepository.findById(id)).thenReturn(Optional.of(assistantUser1));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> assistantService.removeCourseId(id, courseId)
        );
        assertEquals("Course with ID: " + courseId + " does not exist for user with ID: " + id, exception.getMessage());
        verify(userRepository).findById(id);
        verify(userRepository, never()).save(any());
    }
}