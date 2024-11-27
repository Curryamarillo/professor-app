package com.professor.app.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.professor.app.dto.users.ProfessorRequestDTO;
import com.professor.app.dto.users.UserResponseDTO;
import com.professor.app.entities.Professor;
import com.professor.app.exceptions.UserAlreadyExistsException;
import com.professor.app.exceptions.UserNotFoundException;
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

import static org.hamcrest.Matchers.hasItems;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;



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

        mockMvc.perform(post("/api/professor/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(professorRequestDTO1)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").value("User with email: " + professorRequestDTO1.email() + " already exists"));
    }
    @Test
    @DisplayName("Professor get course IDs list by ID successfully")
    public void professorGetCourseByIDSuccessfullyTest() throws Exception {
        String id = "10000";
        Set<String> courseIdList = Set.of("courseId1", "courseId2");
        professorUser1.setCourseIds(courseIdList);
        given(userRepository.findById(id)).willReturn(Optional.of(professorUser1));
        given(professorService.getCourseIdListById(id)).willReturn(courseIdList);

        mockMvc.perform(get("/api/professor/courses/{id}", id)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasItems("courseId1", "courseId2")));
    }
    @Test
    @DisplayName("Professor get course IDs list by ID throws User Not Found Exception")
    public void professorGetCourseByIDThrowsUserNotFoundExceptionTest() throws Exception {
        String id = "90000";

        given(professorService.getCourseIdListById(id)).willThrow(new UserNotFoundException("User with ID: " + id + " does not exist or is not a Professor"));

        mockMvc.perform(get("/api/professor/courses/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
    @Test
    @DisplayName("Add course ID by professor ID successfully")
    public void professorAddCourseByIDByProfessorID() throws Exception {
        String id = "10000";
        String courseToAdd = "courseId3";
        String responseMessage = "Course ID added successfully to user with ID: " + id;
        given(professorService.addCourseIdByProfessorId(id, courseToAdd)).willReturn(responseMessage);

        mockMvc.perform(post("/api/professor/courses/add/{id}", id)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("courseId",courseToAdd))
                .andExpect(status().isOk())
                .andExpect(content().string(responseMessage));
    }
    @Test
    @DisplayName("Add course ID by professor ID throws User Not Found")
    public void professorAddCourseByIDByProfessorIdUserNotFoundTest() throws Exception {
        String id = "10000";
        String courseToAdd = "courseId3";

        given(professorService.addCourseIdByProfessorId(id, courseToAdd))
                .willThrow(new UserNotFoundException("User with id: " + id + " does not exist or is not a Professor"));

        mockMvc.perform(post("/api/professor/courses/add/{id}", id)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("courseId",courseToAdd))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("User with id: " + id + " does not exist or is not a Professor"));
    }
    @Test
    @DisplayName("Delete course ID by professor ID successfully")
    public void deleteCourseIdByProfessorIdSuccessfully() throws Exception {
        String id = "10000";
        String courseToDelete = "courseId1";
        String response = "Course ID removed successfully from user with ID: " + id;

        given(professorService.removeCourseIdById(id, courseToDelete)).willReturn(response);

        mockMvc.perform(delete("/api/professor/courses/delete/{id}", id)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("courseId", courseToDelete))
                .andExpect(status().isOk())
                .andExpect(content().string(response));
    }
    @Test
    @DisplayName("Delete course ID by professor ID throws User Not Found Exception")
    public void deleteCourseIdByProfessorThrowsUserNotFoundException() throws Exception {
        String id = "10000";
        String courseToDelete = "courseId1";
        String response = "User with id: " + id + " does not exist or is not a Professor";

        given(professorService.removeCourseIdById(id, courseToDelete)).willThrow(new UserNotFoundException(response));

        mockMvc.perform(delete("/api/professor/courses/delete/{id}", id)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("courseId", courseToDelete))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("404"))
                .andExpect(jsonPath("$.message").value(response))
                .andExpect(jsonPath("$.timestamp").exists());;
    }
    @Test
    @DisplayName("Get students ID by professor ID successfully")
    public void getStudentsIDByProfessorIDSuccessfully() throws Exception {
        String id = "10000";
        Set<String> studentIdList = Set.of("studentId1", "studentId2");
        professorUser1.setCourseIds(studentIdList);
        given(userRepository.findById(id)).willReturn(Optional.of(professorUser1));
        given(professorService.getStudentIdList(id)).willReturn(studentIdList);

        mockMvc.perform(get("/api/professor/students/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasItems("studentId1", "studentId2")));

    }
    @Test
    @DisplayName("Get students ID by professor ID throws User Not Found Exceptiom")
    public void getStudentsIDByProfessorIDThrowsUserNotFoundException() throws Exception {
        String id = "10000";
        Set<String> studentIdList = Set.of("studentId1", "studentId2");
        professorUser1.setCourseIds(studentIdList);

        String response = "User with id: " + id + " does not exist or is not a Professor";
       given(professorService.getStudentIdList(id)).willThrow(new UserNotFoundException(response));
        mockMvc.perform(get("/api/professor/students/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("404"))
                .andExpect(jsonPath("$.message").value(response))
                .andExpect(jsonPath("$.timestamp").exists());;
    }
    @Test
    @DisplayName("Add one student ID by professor ID successfully")
    public void addOneStudentIdByProfessorIdSuccessfullyTest() throws Exception {
        String id = "10000";
        String studentIdToAdd = "studentId3";
        String responseMessage = "Student ID added successfully to user with ID: " + id;

        given(professorService.addStudentIdByProfessorId(id, studentIdToAdd)).willReturn(responseMessage);

        mockMvc.perform(post("/api/professor/students/{id}", id)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("studentId", studentIdToAdd))
                .andExpect(status().isOk())
                .andExpect(content().string(responseMessage));
    }

    @Test
    @DisplayName("Add one student ID by professor ID throws User Not Found Exception")
    public void addOneStudentIdByProfessorIdThrowsUserNotFoundExceptionTest() throws Exception {
        String id = "10000";
        String studentIdToAdd = "studentId3";
        String errorMessage = "User with ID: " + id + " does not exist or is not a Professor";

        given(professorService.addStudentIdByProfessorId(id, studentIdToAdd))
                .willThrow(new UserNotFoundException(errorMessage));

        mockMvc.perform(post("/api/professor/students/{id}", id)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("studentId", studentIdToAdd))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value(errorMessage));
    }

    @Test
    @DisplayName("Add multiple student IDs by professor ID successfully")
    public void addMultipleStudentIdsByProfessorIdSuccessfullyTest() throws Exception {
        String id = "10000";
        Set<String> studentListToAdd = Set.of("studentId3", "studentId4");
        String responseMessage = "Student IDs added successfully to user with ID: " + id;

        given(professorService.addMultipleStudentsId(id, studentListToAdd)).willReturn(responseMessage);

        mockMvc.perform(post("/api/professor/students/list/{id}", id)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("studentList", String.join(",", studentListToAdd)))
                .andExpect(status().isOk())
                .andExpect(content().string(responseMessage));
    }

    @Test
    @DisplayName("Delete one student ID by professor ID successfully")
    public void deleteOneStudentIdByProfessorIdSuccessfullyTest() throws Exception {
        String id = "10000";
        String studentIdToDelete = "studentId1";
        String responseMessage = "Student ID removed successfully from user with ID: " + id;

        given(professorService.deleteStudentIdByProfessorId(id, studentIdToDelete)).willReturn(responseMessage);

        mockMvc.perform(delete("/api/professor/students/delete/{id}", id)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("studentId", studentIdToDelete))
                .andExpect(status().isOk())
                .andExpect(content().string(responseMessage));
    }

    @Test
    @DisplayName("Delete multiple student IDs by professor ID successfully")
    public void deleteMultipleStudentIdsByProfessorIdSuccessfullyTest() throws Exception {
        String id = "10000";
        Set<String> studentListToDelete = Set.of("studentId1", "studentId2");
        String responseMessage = "Student IDs removed successfully from user with ID: " + id;

        given(professorService.deleteStudentsIdListByProfessorId(id, studentListToDelete)).willReturn(responseMessage);

        mockMvc.perform(delete("/api/professor/students/delete-list/{id}", id)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("studentList", String.join(",", studentListToDelete)))
                .andExpect(status().isOk())
                .andExpect(content().string(responseMessage));
    }

}
