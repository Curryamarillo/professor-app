package com.professor.app.services;

import com.professor.app.dto.users.AdminRequestDTO;
import com.professor.app.dto.users.UserResponseDTO;
import com.professor.app.entities.Admin;
import com.professor.app.entities.Student;
import com.professor.app.exceptions.InvalidUserTypeException;
import com.professor.app.exceptions.UserAlreadyExistsException;
import com.professor.app.exceptions.UserNotFoundException;
import com.professor.app.mapper.AdminMapper;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AdminServiceTests {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AdminService adminService;


    UserResponseDTO userResponseDTO1;
    Admin adminUser1;
    Admin adminUser2;
    Student nonAdmin;
    AdminRequestDTO adminRequestDTO1;

    @BeforeEach

    void setUp() {
        adminUser1 = Admin.builder()
                .id("10000")
                .name("Leonel")
                .surname("Messi")
                .email("campeon10@gmail.com")
                .dni("10000")
                .role(Role.ADMIN)
                .password("password1")
                .createdAt(LocalDateTime.of(2024, 1, 1, 1, 0, 0))
                .modifiedAt(LocalDateTime.of(2024, 1, 1, 1, 1, 10))
                .comments("World Champion")
                .build();

        adminUser2 = Admin.builder()
                .id("10001")
                .name("Fideo")
                .surname("Dimaria")
                .email("campeon11@gmail.com")
                .dni("10002")
                .role(Role.ADMIN)
                .password("password2")
                .createdAt(LocalDateTime.of(2024, 1, 1, 1, 0, 0))
                .modifiedAt(LocalDateTime.of(2024, 1, 1, 1, 1, 10))
                .comments("World Champion")
                .build();

        nonAdmin = Student.builder()
                .id("10003")
                .role(Role.STUDENT)
                .build();

        adminRequestDTO1 = new AdminRequestDTO("Leonel", "Messi", "campeon10@gmail.com", "password1", "10000", "World Champion");


        userResponseDTO1 = new UserResponseDTO("10000", "Leonel", "Messi", "campeon10@gmail.com", "10000", Role.ADMIN);


    }

    @Test
    @DisplayName("Save ADMIN user successfully")
    public void saveAdminUserSuccessfully() {
        String email = "campeon10@gmail.com";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(userRepository.save(adminUser1)).thenReturn(adminUser1);

        try (MockedStatic<AdminMapper> adminMapperMocked = Mockito.mockStatic(AdminMapper.class);
             MockedStatic<UserMapper> userMapperMocked = Mockito.mockStatic(UserMapper.class)) {

            adminMapperMocked.when(() -> AdminMapper.toAdmin(adminRequestDTO1)).thenReturn(adminUser1);
            userMapperMocked.when(() -> UserMapper.toUserResponseDTO(adminUser1)).thenReturn(userResponseDTO1);

            UserResponseDTO result = adminService.saveAdminUser(adminRequestDTO1);

            assertNotNull(result);
            assertEquals("10000", result.id());
            assertEquals("Leonel", result.name());
            assertEquals("Messi", result.surname());
            assertEquals("campeon10@gmail.com", result.email());
            assertEquals(Role.ADMIN, result.role());
        }
    }
    @Test
    @DisplayName("Should throw UserAlreadyExistsException when email already exists")
    public void saveAdminUserShouldThrowUserAlreadyExistsException_WhenEmailAlreadyExists() {
        String email = "campeon10@gmail.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(adminUser1));

        UserAlreadyExistsException exception = assertThrows(UserAlreadyExistsException.class, () -> adminService.saveAdminUser(adminRequestDTO1));

        assertEquals("User with email " + email + " already exists", exception.getMessage());
    }
    @Test
    @DisplayName("Update comments successfully")
    public void updateAdminCommentsSuccessfully() {
        String id = "10000";
        String newComment = "new comment";
        when(userRepository.findById(id)).thenReturn(Optional.of(adminUser1));
        when(userRepository.save(adminUser1)).thenReturn(adminUser1);

        String result = adminService.updateComments(id, newComment);

        assertEquals("new comment", adminUser1.getComments());
        assertEquals("Comments updated successfully for user with id: " + id, result);

    }
    @Test
    @DisplayName("Should throw InvalidUserTypeException if user is not an Admin")
    void shouldThrowInvalidUserTypeExceptionIfUserIsNotAdmin() {
        String userId = "10003";
        String comments = "New comments";

        when(userRepository.findById(userId)).thenReturn(Optional.of(nonAdmin));

        InvalidUserTypeException exception = assertThrows(InvalidUserTypeException.class, () -> adminService.updateComments(userId, comments));

        assertEquals("User with id: " + userId + " is not an Admin", exception.getMessage());
    }
    @Test
    @DisplayName("Update comments throw user not found exception")
    public void updateCommentShouldThrowUserNotFoundExceptionWhenUserDoesNotExist() {
        String id = "10003";
        String comments = "Comments to update";

        when(userRepository.findById(id)).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> adminService.updateComments(id, comments));

        assertEquals("User with id: " + id + " not found", exception.getMessage());
    }

    @Test@DisplayName("get comments successfully")
    public void getCommentsByIdSuccessfully() {
        String id = "10000";
        when(userRepository.findById(id)).thenReturn(Optional.of(adminUser1));

        String result = adminService.getCommentsById(id);

        assertEquals("World Champion", result);
    }
    @Test
    @DisplayName("Get comments throws exception")
    public void getCommentsByIdThrowsException() {
        String id = "not_correct_10000";
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> adminService.getCommentsById(id));
        assertEquals("User with id: not_correct_10000 does not exist or is not an Admin", exception.getMessage());
    }
}
