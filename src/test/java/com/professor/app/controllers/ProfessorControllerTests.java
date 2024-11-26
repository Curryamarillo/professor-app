package com.professor.app.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.professor.app.dto.users.ProfessorRequestDTO;
import com.professor.app.dto.users.UserResponseDTO;
import com.professor.app.entities.Professor;
import com.professor.app.exceptions.UserAlreadyExistsException;
import com.professor.app.repositories.UserRepository;
import com.professor.app.roles.Role;
import com.professor.app.services.ProfessorService;
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
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
public class ProfessorControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProfessorService professorService;

    @MockBean
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    ProfessorRequestDTO professorRequestDTO1;
    UserResponseDTO userResponseDTO1;

    Professor professorUser1;
    Professor professorUser2;

    String courseId1;
    String courseId2;

    String studentId1;
    String studentId2;

    @BeforeEach
    void setUp() {
        courseId1 = "course1";
        courseId2 = "course2";

        studentId1 = "studentId1";
        studentId2 = "studentId2";

        professorUser1 = Professor.builder()
                .id("10000")
                .name("Lionel")
                .surname("Messi")
                .email("campeon10@gmail.com")
                .dni("10000")
                .role(Role.PROFESSOR)
                .password("password1")
                .createdAt(LocalDateTime.of(2024, 1, 1, 1, 0, 0))
                .modifiedAt(LocalDateTime.of(2024, 1, 1, 1, 1, 10))
                .courseIds(new HashSet<>(Set.of(courseId1, courseId2)))
                .studentsIds(new HashSet<>(Set.of(studentId1, studentId2)))
                .build();

        professorUser2 = Professor.builder()
                .id("10001")
                .name("Fideo")
                .surname("Dimaria")
                .email("campeon11@gmail.com")
                .dni("10002")
                .role(Role.ADMIN)
                .password("password2")
                .createdAt(LocalDateTime.of(2024, 1, 1, 1, 0, 0))
                .modifiedAt(LocalDateTime.of(2024, 1, 1, 1, 1, 10))
                .courseIds(new HashSet<>(Set.of(courseId1, courseId2)))
                .studentsIds(new HashSet<>(Set.of(studentId1, studentId2)))
                .build();

        professorRequestDTO1 = new ProfessorRequestDTO("Leonel", "Messi Cuccitini", "campeon10@gmail.com", "password1", "10001", Set.of(courseId1, courseId2), Set.of(studentId1, studentId2));

        userResponseDTO1 = new UserResponseDTO("10000", "Leonel", "Messi", "campeon10@gmail.com", "10000", Role.PROFESSOR);
    }
    @Test
    @DisplayName("Professor created successfully")
    public void professorCreatedSuccessfullyTest() throws Exception {
        given(professorService.saveProfessorUser(professorRequestDTO1)).willReturn(userResponseDTO1);

        mockMvc.perform(post("/api/professor/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(professorRequestDTO1)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(userResponseDTO1.id()))
                .andExpect(jsonPath("$.name").value(userResponseDTO1.name()))
                .andExpect(jsonPath("$.surname").value(userResponseDTO1.surname()))
                .andExpect(jsonPath("$.email").value(userResponseDTO1.email()))
                .andExpect(jsonPath("$.dni").value(userResponseDTO1.dni()))
                .andExpect(jsonPath("$.role").value("PROFESSOR"));;
    }
    @Test
    @DisplayName("Professor created not successfully throws exception")
    public void professorCreatedThrowsUserNotCreatedExceptionTest() throws Exception {

        given(userRepository.findByEmail(professorRequestDTO1.email()))
                .willReturn(Optional.of(professorUser1));
        given(professorService.saveProfessorUser(professorRequestDTO1))
                .willThrow(new UserAlreadyExistsException("User with email: " + professorRequestDTO1.email() + " already exists"));

        mockMvc.perform(post("/api/assistant/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(professorRequestDTO1)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").value("User with email: " + professorRequestDTO1.email() + " already exists"));
    }
}
