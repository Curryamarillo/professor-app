package com.professor.app.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.professor.app.dto.users.AdminRequestDTO;
import com.professor.app.dto.users.UserResponseDTO;
import com.professor.app.dto.users.UserUpdateDTO;
import com.professor.app.entities.Admin;
import com.professor.app.exceptions.UserAlreadyExistsException;
import com.professor.app.exceptions.UserNotFoundException;
import com.professor.app.repositories.UserRepository;
import com.professor.app.services.AdminService;
import com.professor.app.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.professor.app.roles.Role.ADMIN;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AdminControllerTests {


    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private AdminService adminService;

    @MockBean
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    UserResponseDTO userResponseDTO1;
    UserResponseDTO userResponseDTO2;
    Admin adminUser1;
    Admin adminUser2;

    AdminRequestDTO adminRequestDTO1;
    UserUpdateDTO adminUpdateRequestDTO;

    @BeforeEach
    void setUp() {
        adminUser1 = Admin.builder()
                .id("10000")
                .name("Leonel")
                .surname("Messi")
                .email("campeon10@gmail.com")
                .dni("10000")
                .role(ADMIN)
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
                .role(ADMIN)
                .password("password2")
                .createdAt(LocalDateTime.of(2024, 1, 1, 1, 0, 0))
                .modifiedAt(LocalDateTime.of(2024, 1, 1, 1, 1, 10))
                .comments("World Champion")
                .build();
        adminRequestDTO1 = new AdminRequestDTO(
                "Leonel",
                "Messi",
                "campeon10@gmail.com",
                "password1",
                "10000",
                "World champion"
        );
        adminUpdateRequestDTO = new UserUpdateDTO(
                "Lionel",
                "Messi Cuccitini",
                "campeónDeTodo",
                "10"
        );

        userResponseDTO1 = new UserResponseDTO("10000", "Leonel", "Messi", "campeon10@gmail.com", "10000", ADMIN);
        userResponseDTO2 = new UserResponseDTO("10001", "Fideo", "Dimaria", "campeon11@gmail.com", "10000", ADMIN);
    }
    @Test
    @DisplayName("Create Admin user successfully")
    void createAdminUserSuccessfully() throws Exception {
        given(adminService.saveAdminUser(adminRequestDTO1)).willReturn(userResponseDTO1);

       mockMvc.perform(post("/api/admin/create")
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(adminRequestDTO1)))
               .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(userResponseDTO1.id()))
                .andExpect(jsonPath("$.name").value(userResponseDTO1.name()))
                .andExpect(jsonPath("$.surname").value(userResponseDTO1.surname()))
                .andExpect(jsonPath("$.email").value(userResponseDTO1.email()))
                .andExpect(jsonPath("$.dni").value(userResponseDTO1.dni()))
                .andExpect(jsonPath("$.role").value("ADMIN"));
    }
    @Test
    @DisplayName("Create admin user throws UserAlreadyExistsException")
    void createAdminUserThrowsUserAlreadyExistsException() throws Exception {

        given(userRepository.findByEmail(adminRequestDTO1.email()))
                .willReturn(Optional.of(adminUser1));

        given(adminService.saveAdminUser(adminRequestDTO1))
                .willThrow(new UserAlreadyExistsException("User with email " + adminRequestDTO1.email() + " already exists"));

        mockMvc.perform(post("/api/admin/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(adminRequestDTO1)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").value("User with email " + adminRequestDTO1.email() + " already exists"));
    }
    @Test
    @DisplayName("Update comments successfully")
    void updateCommentsByIdSuccessfully() throws Exception {
        String id = "10000";
        String commentsToUpdate = "GOAT";

        given(userRepository.findById(id)).willReturn(Optional.of(adminUser1));
        given(adminService.updateComments(id, commentsToUpdate)).willReturn("Comments updated successfully for user with id: " + id);

        mockMvc.perform(patch("/api/admin/update/comments/{id}", id)
                .param("comments", commentsToUpdate)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Comments updated successfully for user with id: " + id));
    }

    @Test
    @DisplayName("Get comments successfully")
    void getCommentsSuccessfully() throws Exception {
        String id = "10000";

        given(userRepository.findById(id)).willReturn(Optional.of(adminUser1));
        given(adminService.getComments(id)).willReturn(adminUser1.getComments());

        mockMvc.perform(get("/api/admin/comments/{id}", id)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("World Champion"));
    }

}
