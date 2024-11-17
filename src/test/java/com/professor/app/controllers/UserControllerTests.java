package com.professor.app.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.professor.app.dto.users.AdminRequestDTO;
import com.professor.app.dto.users.UpdatePasswordDTO;
import com.professor.app.dto.users.UserResponseDTO;
import com.professor.app.dto.users.UserUpdateDTO;
import com.professor.app.entities.Admin;
import com.professor.app.exceptions.UserNotFoundException;
import com.professor.app.repositories.UserRepository;
import com.professor.app.roles.Role;
import com.professor.app.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserRepository userRepository;

    UserResponseDTO userResponseDTO1;
    UserResponseDTO userResponseDTO2;
    Admin adminUser1;
    Admin adminUser2;

    UserUpdateDTO userUpdateDTO;

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
        userUpdateDTO = new UserUpdateDTO(
                "Leonel",
                "Messi",
                "campeon10@gmail.com",
                "password1"
        );

        userResponseDTO1 = new UserResponseDTO("10000", "Leonel", "Messi", "campeon10@gmail.com", "10000", Role.ADMIN);
        userResponseDTO2 = new UserResponseDTO("10001", "Fideo", "Dimaria", "campeon11@gmail.com", "10000", Role.ADMIN);

    }

    @Test
    @DisplayName("Get all users success")
    void getAllUsersTest() throws Exception {
        List<UserResponseDTO> userResponseDTOList = List.of(userResponseDTO1, userResponseDTO2);

        given(userService.findAllUsers()).willReturn(userResponseDTOList);

        mockMvc.perform(get("/api/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].id").value("10000"))
                .andExpect(jsonPath("$[0].name").value("Leonel"))
                .andExpect(jsonPath("$[0].surname").value("Messi"))
                .andExpect(jsonPath("$[0].email").value("campeon10@gmail.com"))
                .andExpect(jsonPath("$[0].role").value("ADMIN"))
                .andExpect(jsonPath("$[1].id").value("10001"))
                .andExpect(jsonPath("$[1].name").value("Fideo"))
                .andExpect(jsonPath("$[1].surname").value("Dimaria"))
                .andExpect(jsonPath("$[1].email").value("campeon11@gmail.com"))
                .andExpect(jsonPath("$[1].role").value("ADMIN"));
    }

    @Test
    @DisplayName("Get user by ID success")
    void getUserById() throws Exception {
        String id = "10000";


        given(userService.findUserById(id)).willReturn(userResponseDTO1);

        mockMvc.perform(get("/api/users/id/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("10000"))
                .andExpect(jsonPath("$.name").value("Leonel"))
                .andExpect(jsonPath("$.surname").value("Messi"))
                .andExpect(jsonPath("$.email").value("campeon10@gmail.com"))
                .andExpect(jsonPath("$.role").value("ADMIN"));
    }

    @Test
    @DisplayName("Get user by id not found")
    void userByIdReturnNotFoundWhenUserDoesNotExist() throws Exception {
        String id = "99999";
        given(userService.findUserById(id)).willThrow(new UserNotFoundException("User with id: " + id + " not found"));

        mockMvc.perform(get("/api/users/id/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("User with id: " + id + " not found"))
                .andExpect(jsonPath("timestamp").exists());
    }

    @Test
    @DisplayName("Get users by role successfully")
    void getUsersByRoleSuccessfully() throws Exception {
        String role = "ADMIN";
        List<UserResponseDTO> userResponseDTOList = List.of(userResponseDTO1, userResponseDTO2);

        given(userService.findUsersByRole(role)).willReturn(userResponseDTOList);

        mockMvc.perform(get("/api/users/role/{role}", role)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].id").value("10000"))
                .andExpect(jsonPath("$[0].name").value("Leonel"))
                .andExpect(jsonPath("$[0].surname").value("Messi"))
                .andExpect(jsonPath("$[0].email").value("campeon10@gmail.com"))
                .andExpect(jsonPath("$[0].role").value("ADMIN"))
                .andExpect(jsonPath("$[1].id").value("10001"))
                .andExpect(jsonPath("$[1].name").value("Fideo"))
                .andExpect(jsonPath("$[1].surname").value("Dimaria"))
                .andExpect(jsonPath("$[1].email").value("campeon11@gmail.com"))
                .andExpect(jsonPath("$[1].role").value("ADMIN"));

    }
    @Test
    @DisplayName("Update Admin user by ID successfully")
    void updateAdminByIdSuccessfully() throws Exception {
        String id = "10000";
        UserUpdateDTO updateRequestDTO = new UserUpdateDTO("Lionel", "Messi Cuccitini", "campeónDeTodo", "10");

        given(userRepository.findById(id)).willReturn(Optional.of(adminUser1));

        given(userService.updateUser(id, updateRequestDTO)).willReturn("User with id: " + id + " updated successfully");

        mockMvc.perform(put("/api/users/update/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(content().string("User with id: " + id + " updated successfully"));
    }

    @Test
    @DisplayName("Update admin by ID exception")
    void updateAdminByIdThrowsException() throws Exception{
        String id = "20000";
        given(userRepository.findById(id)).willReturn(Optional.empty());
        given(userService.updateUser(id, userUpdateDTO)).willThrow(new UserNotFoundException("User with id: " + id + " not found"));

        mockMvc.perform(put("/api/users/update/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userUpdateDTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("404"))
                .andExpect(jsonPath("$.message").value("User with id: " + id + " not found"))
                .andExpect(jsonPath("$.timestamp").exists());
    }
    @Test
    @DisplayName("Update password successfully")
    void updatePasswordSuccessfully() throws Exception {
        String id = "10000";
        UpdatePasswordDTO updatePasswordDTO = new UpdatePasswordDTO("oldPassword", "newPassword");
        String successMessage = "User with id: " + id + " password updated successfully";

        given(userService.updatePassword(id, updatePasswordDTO.oldPassword(), updatePasswordDTO.newPassword()))
                .willReturn(successMessage);

        mockMvc.perform(put("/api/users/{id}/update-password", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(updatePasswordDTO)))
                .andExpect(status().isOk())
                .andExpect(content().string(successMessage));
    }

    @Test
    @DisplayName("Update password mismatch")
    void shouldThrowExceptionWhenOldPasswordDoesNotMatch() throws Exception {
        String id = "10000";
        UpdatePasswordDTO request = new UpdatePasswordDTO("wrongOldPassword", "newPassword456");
        String errorMessage = "Old password does not match";

        given(userService.updatePassword(id, request.oldPassword(), request.newPassword()))
                .willThrow(new IllegalArgumentException(errorMessage));

        mockMvc.perform(put("/api/users/{id}/update-password", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(errorMessage))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @DisplayName("Delete user successfully")
    void deleteUserSuccessfully() throws Exception {
        String id = "10000";

        given(userService.deleteUser(id)).willReturn("User with id: " + id + " has been deleted successfully");

        mockMvc.perform(delete("/api/users/delete/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("User with id: " + id + " has been deleted successfully"));
    }

    @Test
    @DisplayName("Delete user throws exception when user not found")
    void deleteUserNotFound() throws Exception {
        String id = "12000";

        given(userService.deleteUser(id)).willThrow(new UserNotFoundException("User with id: " + id + " not found"));

        mockMvc.perform(delete("/api/users/delete/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("User with id: " + id + " not found"))
                .andExpect(jsonPath("$.status").value(404));
    }
}
