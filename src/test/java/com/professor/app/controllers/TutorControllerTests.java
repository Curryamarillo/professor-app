package com.professor.app.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.professor.app.dto.users.TutorRequestDTO;
import com.professor.app.dto.users.UserResponseDTO;
import com.professor.app.entities.Tutor;
import com.professor.app.exceptions.UserAlreadyExistsException;
import com.professor.app.exceptions.UserNotFoundException;
import com.professor.app.repositories.UserRepository;
import com.professor.app.roles.Role;
import com.professor.app.services.TutorService;
import com.professor.app.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.*;


import static org.hamcrest.Matchers.hasItems;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
@Import(TutorController.class)
public class TutorControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private TutorService tutorService;

    @MockBean
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    TutorRequestDTO tutorRequestDTO;
    UserResponseDTO userResponseDTO;
    Tutor tutorUser1;
    Tutor tutorUser2;
    String studentId1;
    String studentId2;
    String studentId3;


    @BeforeEach
    void setUp() {
        studentId1 = "studentId1";
        studentId2 = "studentId2";
        studentId3 = "studentId3";

        tutorUser1 = Tutor.builder().id("10000").name("Lionel").surname("Messi").email("campeon10@gmail.com").dni("10000").role(Role.PROFESSOR).password("password1").createdAt(LocalDateTime.of(2024, 1, 1, 1, 0, 0)).modifiedAt(LocalDateTime.of(2024, 1, 1, 1, 1, 10)).tutoredStudentsId(new HashSet<>(Set.of(studentId1, studentId2, studentId3))).build();

        tutorUser2 = Tutor.builder().id("10001").name("Fideo").surname("Dimaria").email("campeon11@gmail.com").dni("10002").role(Role.ADMIN).password("password2").createdAt(LocalDateTime.of(2024, 1, 1, 1, 0, 0)).modifiedAt(LocalDateTime.of(2024, 1, 1, 1, 1, 10)).tutoredStudentsId(new HashSet<>(Set.of(studentId1, studentId2, studentId3))).build();

        tutorRequestDTO = new TutorRequestDTO("Leonel", "Messi Cuccitini", "campeon10@gmail.com", "password1", "10001", Set.of(studentId1, studentId2));

        userResponseDTO = new UserResponseDTO("10000", "Leonel", "Messi", "campeon10@gmail.com", "10000", Role.TUTOR);
    }
    @Test
    @DisplayName("Create Tutor User successfully")
    void createTutorUserSuccessfullyTest() throws Exception {
        given(tutorService.saveTutorUser(tutorRequestDTO)).willReturn(userResponseDTO);

        mockMvc.perform(post("/api/tutor/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tutorRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(userResponseDTO.id()))
                .andExpect(jsonPath("$.name").value(userResponseDTO.name()))
                .andExpect(jsonPath("$.surname").value(userResponseDTO.surname()))
                .andExpect(jsonPath("$.email").value(userResponseDTO.email()))
                .andExpect(jsonPath("$.dni").value(userResponseDTO.dni()))
                .andExpect(jsonPath("$.role").value("TUTOR"));
    }
    @Test
    @DisplayName("Create Tutor throws User Already Exists Exception")
    void createTutorThrowsUserAlreadyExistsExceptionTest() throws Exception {
        given(userRepository.findByEmail(tutorUser1.getEmail())).willReturn(Optional.of(tutorUser1));
        given(tutorService.saveTutorUser(tutorRequestDTO)).willThrow(new UserAlreadyExistsException("User with email " + tutorRequestDTO.email() + " already exists"));

        mockMvc.perform(post("/api/tutor/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tutorRequestDTO)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").value("User with email " + tutorRequestDTO.email() + " already exists"));
    }
    @Test
    @DisplayName("Get students list successfully")
    void getStudentIdListSuccessfully() throws Exception {
        String id = "10000";
        Set<String> studentList = Set.of("studentId1", "studentId2", "studentId3");
        tutorUser1.setTutoredStudentsId(studentList);
        given(userRepository.findById(id)).willReturn(Optional.of(tutorUser1));
        given(tutorService.getStudentsIDsByTutorId(id)).willReturn(tutorUser1.getTutoredStudentsId());

        mockMvc.perform(get("/api/tutor/student-list/{id}", id)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasItems("studentId1", "studentId2", "studentId3")));

    }
    @Test
    @DisplayName("Get students list throws User Not Found")
    void getStudentIdListThrowsUserNotFoundTest() throws Exception {
        String id = "10000";
        Set<String> studentList = Set.of("studentId1", "studentId2", "studentId3");
        tutorUser1.setTutoredStudentsId(studentList);

        String response = "User with id: " + id + " does not exist or is not a Tutor";
        given(tutorService.getStudentsIDsByTutorId(id)).willThrow(new UserNotFoundException(response));

        mockMvc.perform(get("/api/tutor/student-list/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("404"))
                .andExpect(jsonPath("$.message").value(response))
                .andExpect(jsonPath("$.timestamp").exists());;
    }
    @Test
    @DisplayName("Add student id by Tutor ID successfully")
    void addStudentIdByTutorIDSuccessfully() throws Exception {
        String id = "10000";
        String studentId = "studentId1";
        String responseMessage = "Student ID added successfully to user with ID: " + id;

        given(tutorService.addStudentIDByTutorID(id, studentId)).willReturn(responseMessage);

        mockMvc.perform(post("/api/tutor/student/{id}", id)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("studentId", studentId))
                .andExpect(status().isOk())
                .andExpect(content().string(responseMessage));
    }
    @Test
    @DisplayName("Add student id by Tutor ID throws User Not Found Exception")
    void addStudentIdByTutorIDThrowsUserNotFoundException() throws Exception {
        String id = "10000";
        String studentId = "studentId1";
        String errorMessage = "User with ID: " + id + " does not exist or is not a Professor";

        given(tutorService.addStudentIDByTutorID(id, studentId)).willThrow(new UserNotFoundException(errorMessage));

        mockMvc.perform(post("/api/tutor/student/{id}", id)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("studentId", studentId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value(errorMessage));

    }
    @Test
    @DisplayName("Add student id list by Tutor ID successfully")
    void addStudentIdListByTutorIDSuccessfully() throws Exception {
        String id = "10000";
        List<String> studentIdList = List.of("studentId1", "studentId2", "studentId3");
        String responseMessage = "Student IDs added successfully to user with ID: " + id;

        given(tutorService.addMultipleStudentsIDByTutorID(id, studentIdList)).willReturn(responseMessage);

        mockMvc.perform(post("/api/tutor/student-list/{id}", id)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("studentIdList", String.join(",", studentIdList)))
                .andExpect(status().isOk())
                .andExpect(content().string(responseMessage));
    }
    @Test
    @DisplayName("Add student id list by Tutor Id throws User Not Found Exception")
    void addStudentIdListByTutorIDThrowsUserNotFoundException() throws Exception {
        String id = "10000";
        List<String> studentIdList = List.of("studentId1", "studentId2", "studentId3");
        String responseMessage ="User with ID: " + id + " not found or is not a Tutor";

        given(tutorService.addMultipleStudentsIDByTutorID(id, studentIdList)).willThrow(new UserNotFoundException(responseMessage));

        mockMvc.perform(post("/api/tutor/student-list/{id}", id)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("studentIdList", String.join(",", studentIdList)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value(responseMessage));
    }
    @Test
    @DisplayName("Delete student id by Tutor ID successfully")
    void deleteStudentIdByTutorIDSuccessfully() throws Exception {
        String id = "10000";
        String studentIdToDelete = "studentId1";
        String responseMessage = "Student successfully removed to user with ID: " + id;

        given(tutorService.deleteStudentIDByTutorId(id, studentIdToDelete)).willReturn(responseMessage);

        mockMvc.perform(delete("/api/tutor/student/{id}", id)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("studentId", studentIdToDelete))
                .andExpect(status().isOk())
                .andExpect(content().string(responseMessage));
    }
    @Test
    @DisplayName("Delete student id by Tutor ID throws User Not Found Exception")
    void deleteStudentIdByTutorIDThrowsUserNotFoundExceptionTest() throws Exception {
        String id = "10000";
        String studentIdToDelete = "studentId1";
        String responseMessage = "User with ID: " + id + " not found or is not a Tutor";

        given(tutorService.deleteStudentIDByTutorId(id, studentIdToDelete)).willThrow(new UserNotFoundException(responseMessage));

        mockMvc.perform(delete("/api/tutor/student/{id}", id)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("studentId", studentIdToDelete))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value(responseMessage))
                .andExpect(jsonPath("$.timestamp").exists());;
    }
    @Test
    @DisplayName("Delete Student ID list by Tutor ID successfully")
    void deleteStudentIDByListByTutorIDSuccessfully() throws Exception {
        String id = "10000";
        Set<String> studentIdList = Set.of("studentId1", "studentId2", "studentId3");
        String responseMessage = "All student IDs successfully deleted from user with ID: " + id;

        given(tutorService.deleteStudentsIDListByTutorID(id, studentIdList)).willReturn(responseMessage);
        given(userRepository.findById(id)).willReturn(Optional.of(tutorUser1));

        mockMvc.perform(delete("/api/tutor/student-list/{id}", id)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("studentIdListToDelete", "studentId1","studentId2", "studentId3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(responseMessage));
    }
}
