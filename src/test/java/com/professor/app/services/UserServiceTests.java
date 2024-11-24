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
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTests {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private UserResponseDTO userResponseDTO1;
    private UserResponseDTO userResponseDTO2;
    private Admin adminUser1;
    private Admin adminUser2;

    @BeforeEach
    void setUp() {
        adminUser1 = buildAdmin("10000", "Leonel", "Messi", "campeon10@gmail.com", "10000", "password1");
        adminUser2 = buildAdmin("10001", "Fideo", "Dimaria", "campeon11@gmail.com", "10002", "password2");

        userResponseDTO1 = new UserResponseDTO("10000", "Leonel", "Messi", "campeon10@gmail.com", "10000", Role.ADMIN);
        userResponseDTO2 = new UserResponseDTO("10001", "Fideo", "Dimaria", "campeon11@gmail.com", "10000", Role.ADMIN);
    }

    private Admin buildAdmin(String id, String name, String surname, String email, String dni, String password) {
        return Admin.builder()
                .id(id)
                .name(name)
                .surname(surname)
                .email(email)
                .dni(dni)
                .role(Role.ADMIN)
                .password(password)
                .createdAt(LocalDateTime.of(2024, 1, 1, 1, 0, 0))
                .modifiedAt(LocalDateTime.of(2024, 1, 1, 1, 1, 10))
                .comments("World Champion")
                .build();
    }

    @Test
    @DisplayName("Find all users test")
    void findAllUsersTest() {
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
    void findUserByIdTest() {
        String id = "10000";
        when(userRepository.findById(id)).thenReturn(Optional.of(adminUser1));

        Optional<UserResponseDTO> result = Optional.ofNullable(userService.findUserById(id));

        assertTrue(result.isPresent());
        assertEquals(id, result.get().id());
        verifyUserDetails(result.get(), adminUser1);
    }

    private void verifyUserDetails(UserResponseDTO result, User expected) {
        assertEquals(expected.getName(), result.name());
        assertEquals(expected.getSurname(), result.surname());
        assertEquals(expected.getEmail(), result.email());
        assertEquals(expected.getDni(), result.dni());
        assertEquals(expected.getRole().toString(), result.role().toString());
    }

    @Test
    @DisplayName("Find user by id throws exception test")
    void findUserByIdNotFoundTest() {
        String id = "nonExistindId";
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.findUserById(id))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("User with ID: " + id + " not found");
    }

    @Test
    @DisplayName("Find users by role test")
    void findUsersByRole() {
        String role = "ADMIN";
        List<User> userList = Arrays.asList(adminUser1, adminUser2);
        when(userRepository.findUsersByRole(role)).thenReturn(userList);

        List<UserResponseDTO> result = userService.findUsersByRole(role);

        assertEquals(2, result.size());
        verifyUserRole(result, Role.ADMIN);
        verify(userRepository).findUsersByRole(role);
    }

    private void verifyUserRole(List<UserResponseDTO> users, Role expectedRole) {
        users.forEach(user -> assertEquals(expectedRole.toString(), user.role().toString()));
    }

    @Test
    @DisplayName("Find users by role throws exception when no users found test")
    void findUsersByRoleNotFoundTest() {
        String role = "NON_EXISTING_ROLE";
        when(userRepository.findUsersByRole(role)).thenReturn(Collections.emptyList());

        assertThatThrownBy(() -> userService.findUsersByRole(role))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("No users found with role: " + role);

        verify(userRepository).findUsersByRole(role);
    }

    @Test
    @DisplayName("Update password successfully test")
    void updatePasswordTest() {
        String id = "10000";
        String newPassword = "new_password";
        String oldPassword = "password1";

        when(userRepository.findById(id)).thenReturn(Optional.of(adminUser1));

        String result = userService.updatePassword(id, oldPassword, newPassword);

        assertEquals("Password updated successfully for user with ID: " + id, result);
        assertEquals(newPassword, adminUser1.getPassword());
        verify(userRepository).save(adminUser1);
    }

    @Test
    @DisplayName("Delete user successfully test")
    void deleteUserSuccessfullyTest() {
        String id = "1";
        when(userRepository.findById(id)).thenReturn(Optional.of(adminUser1));

        String result = userService.deleteUser(id);

        assertEquals("User with ID: " + id + " deleted successfully", result);
        verify(userRepository).deleteById(id);
    }

    @Test
    @DisplayName("Delete user not found exception test")
    void deleteUserNotFoundExceptionTest() {
        String id = "1";
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.deleteUser(id))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("User with ID: " + id + " not found");
    }
}
