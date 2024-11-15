package com.professor.app.services;


import com.professor.app.dto.users.UserResponseDTO;
import com.professor.app.entities.Admin;
import com.professor.app.entities.User;
import com.professor.app.exceptions.UserNotFoundException;
import com.professor.app.repositories.UserRepository;
import com.professor.app.roles.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTests {




    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;


    UserResponseDTO userResponseDTO1;
    UserResponseDTO userResponseDTO2;
    Admin adminUser1;
    Admin adminUser2;

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

        userResponseDTO1 = new UserResponseDTO("10000", "Leonel", "Messi", "campeon10@gmail.com", "10000", Role.ADMIN);
        userResponseDTO2 = new UserResponseDTO("10001", "Fideo", "Dimaria", "campeon11@gmail.com", "10000", Role.ADMIN);


    }
    @Test
    @DisplayName("Find all users test")
    public void findAllUsersTest() {
        List<User> userList = Arrays.asList(adminUser1, adminUser2);

        when(userRepository.findAll()).thenReturn(userList);

        List<UserResponseDTO> result = userService.findAllUsers();

        assertEquals(2, result.size());
        assertEquals("Leonel", result.get(0).name());
        assertEquals("Fideo", result.get(1).name());

        verify(userRepository).findAll();
    }

    @Test
    @DisplayName("Find user by id test")
    public void findUserByIdTest() {
        String id = "10000";

        when(userRepository.findById(id)).thenReturn(Optional.of(adminUser1));

        Optional<UserResponseDTO> result = userService.findUserById(id);

        assertTrue(result.isPresent());
        assertEquals(id, result.get().id());
        assertEquals(adminUser1.getName(), result.get().name());
        assertEquals(adminUser1.getSurname(), result.get().surname());
        assertEquals(adminUser1.getEmail(), result.get().email());
        assertEquals(adminUser1.getDni(), result.get().dni());
        assertEquals(adminUser1.getRole().toString(), result.get().role().toString());

    }
    @Test
    @DisplayName("Find user by id throws exception")
    public void findUserByIdNotFoundTest() {
        String id = "nonExistindId";
        Mockito.when(userRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.findUserById(id))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("User with id: " + id + " not found");


    }

    @Test
    @DisplayName("Find users by role")
    public void findUsersByRole() {
        String role = "ADMIN";
        List<User> userList = Arrays.asList(adminUser1, adminUser2);

        when(userRepository.findUsersByRole(role)).thenReturn(userList);

        List<UserResponseDTO> result = userService.findUsersByRole(role);

        assertEquals(2, result.size());
        assertEquals("Leonel", result.get(0).name());
        assertEquals("Fideo", result.get(1).name());
        assertEquals(Role.ADMIN.toString(), result.get(0).role().toString());
        assertEquals(Role.ADMIN.toString(), result.get(1).role().toString());

        verify(userRepository).findUsersByRole(role);
    }

    @Test
    @DisplayName("Find users by role throws exception when no users found")
    public void findUsersByRoleNotFoundTest() {
        String role = "NON_EXISTING_ROLE";

        when(userRepository.findUsersByRole(role)).thenReturn(Collections.emptyList());

        assertThatThrownBy(() -> userService.findUsersByRole(role))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("User with role: " + role + " not found");

        verify(userRepository).findUsersByRole(role);
    }

    @Test
    @DisplayName("Update password successfully")
    public void updatePassword() {
        String id = "10000";
        String newPassword = "new_password";
        String oldPassword = adminUser1.getPassword();

        when(userRepository.findById(id)).thenReturn(Optional.of(adminUser1));

        String result = userService.updatePassword(id, newPassword, oldPassword);

        assertEquals("User with id: " + id + " password updated successfully", result);
        assertEquals(newPassword, adminUser1.getPassword());
        verify(userRepository).save(adminUser1);
    }

    @Test
    @DisplayName("Delete user successfully")
    public void deleteUserSuccessfully() {
        String id = "1";

        when(userRepository.findById(id)).thenReturn(Optional.ofNullable(adminUser1));

        String result = userService.deleteUser(id);


        assertEquals("User with id: " + id + " has been deleted successfully", result);
        verify(userRepository).deleteById(id);
    }
    @Test
    @DisplayName("Fail to update password if old password is incorrect")
    public void updatePassword_FailOldPasswordIncorrect() {
        String id = "10000";
        String oldPassword = "wrong_password";
        String newPassword = "new_password";

        when(userRepository.findById(id)).thenReturn(Optional.of(adminUser1));

        assertThrows(IllegalArgumentException.class, () -> userService.updatePassword(id, newPassword, oldPassword));
        verify(userRepository, never()).save(adminUser1);
    }
    @Test
    @DisplayName("Fail to update password if user not found")
    public void updatePassword_UserNotFound() {
        String id = "10000";
        String oldPassword = "password1";
        String newPassword = "new_password";

        when(userRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.updatePassword(id, newPassword, oldPassword));
        verify(userRepository, never()).save(any(Admin.class));
    }

    @Test
    @DisplayName("Delete user not found exception")
    public void deleteUserNotFoundException() {
        String id = "1";

        when(userRepository.findById(id)).thenReturn(Optional.empty());


        assertThatThrownBy(() -> userService.deleteUser(id))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("User with id: " + id + " not found");

    }
}
