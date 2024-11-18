package com.professor.app.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.professor.app.dto.users.AdminRequestDTO;
import com.professor.app.dto.users.UserResponseDTO;
import com.professor.app.dto.users.UserUpdateDTO;
import com.professor.app.entities.Admin;
import com.professor.app.entities.Assistant;
import com.professor.app.repositories.UserRepository;
import com.professor.app.services.AdminService;
import com.professor.app.services.AssistantService;
import com.professor.app.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static com.professor.app.roles.Role.ADMIN;

@SpringBootTest
@AutoConfigureMockMvc
public class AssistantControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private AssistantService assistanceService;

    @MockBean
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    UserResponseDTO userResponseDTO1;
    UserResponseDTO userResponseDTO2;
    Assistant assistantUser1;
    Assistant assistantUser2;

    AdminRequestDTO adminRequestDTO1;
    UserUpdateDTO adminUpdateRequestDTO;

    @BeforeEach
    void setUp() {
        assistantUser1 = Assistant.builder()
                .id("10000")
                .name("Leonel")
                .surname("Messi")
                .email("campeon10@gmail.com")
                .dni("10000")
                .role(ADMIN)
                .password("password1")
                .createdAt(LocalDateTime.of(2024, 1, 1, 1, 0, 0))
                .modifiedAt(LocalDateTime.of(2024, 1, 1, 1, 1, 10))
                .build();

        assistantUser2 = Assistant.builder()
                .id("10001")
                .name("Fideo")
                .surname("Dimaria")
                .email("campeon11@gmail.com")
                .dni("10002")
                .role(ADMIN)
                .password("password2")
                .createdAt(LocalDateTime.of(2024, 1, 1, 1, 0, 0))
                .modifiedAt(LocalDateTime.of(2024, 1, 1, 1, 1, 10))

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
}
