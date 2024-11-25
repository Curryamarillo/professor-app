package com.professor.app.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.professor.app.dto.users.ProfessorRequestDTO;
import com.professor.app.dto.users.UserResponseDTO;
import com.professor.app.entities.Professor;
import com.professor.app.repositories.UserRepository;
import com.professor.app.roles.Role;
import com.professor.app.services.ProfessorService;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

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
}
