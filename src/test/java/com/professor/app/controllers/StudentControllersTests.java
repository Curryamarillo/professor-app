package com.professor.app.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.professor.app.dto.users.StudentRequestDTO;
import com.professor.app.dto.users.UserResponseDTO;
import com.professor.app.entities.Student;
import com.professor.app.exceptions.UserAlreadyExistsException;
import com.professor.app.exceptions.UserNotFoundException;
import com.professor.app.repositories.UserRepository;
import com.professor.app.roles.Role;
import com.professor.app.services.StudentService;
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
public class StudentControllersTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StudentService studentService;

    @MockBean
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    StudentRequestDTO studentRequestDTO1;
    UserResponseDTO userResponseDTO1;

    Student studentUser1;
    Student studentUser2;
    Student studentUser3;

    String courseId1;
    String courseId2;

    @BeforeEach
    void setUp() {

        courseId1 = "course1";
        courseId2 = "course2";

        studentUser1 = Student.builder().id("10000").name("Lionel").surname("Messi").email("campeon10@gmail.com").dni("10000").role(Role.STUDENT).password("password1").createdAt(LocalDateTime.of(2024, 1, 1, 1, 0, 0)).modifiedAt(LocalDateTime.of(2024, 1, 1, 1, 1, 10)).enrolledCoursesId(new HashSet<>(Set.of(courseId1, courseId2))).build();
        studentUser2 = Student.builder().id("10001").name("Fideo").surname("Dimaria").email("campeon11@gmail.com").dni("10002").role(Role.STUDENT).password("password2").createdAt(LocalDateTime.of(2024, 1, 1, 1, 0, 0)).modifiedAt(LocalDateTime.of(2024, 1, 1, 1, 1, 10)).enrolledCoursesId(new HashSet<>(Set.of(courseId1, courseId2))).build();
        studentUser3 = Student.builder().id("10002").name("Alexis").surname("MacAllister").email("campeon12@gmail.com").dni("10003").role(Role.STUDENT).password("password3").createdAt(LocalDateTime.of(2024, 1, 1, 1, 0, 0)).modifiedAt(LocalDateTime.of(2024, 1, 1, 1, 1, 10)).enrolledCoursesId(new HashSet<>(Set.of(courseId1, courseId2))).build();

        studentRequestDTO1 = new StudentRequestDTO("Leonel", "Messi", "campeon10@gmail.com", "password1", "10000", Set.of(courseId1, courseId2));
        userResponseDTO1 = new UserResponseDTO("10000", "Leonel", "Messi", "campeon10@gmail.com", "10000", Role.STUDENT);

    }
    @Test
    @DisplayName("Student created successfully")
    public void studentCreatedSuccessfullyTest() throws Exception {
        given(studentService.saveStudentUser(studentRequestDTO1)).willReturn(userResponseDTO1);

        mockMvc.perform(post("/api/student/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(studentRequestDTO1)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(userResponseDTO1.id()))
                .andExpect(jsonPath("$.name").value(userResponseDTO1.name()))
                .andExpect(jsonPath("$.surname").value(userResponseDTO1.surname()))
                .andExpect(jsonPath("$.email").value(userResponseDTO1.email()))
                .andExpect(jsonPath("$.dni").value(userResponseDTO1.dni()))
                .andExpect(jsonPath("$.role").value("STUDENT"));;
    }
    @Test
    @DisplayName("Student created not successfully throws exception")
    public void studentCreatedThrowsUserNotCreatedExceptionTest() throws Exception {

        given(userRepository.findByEmail(studentRequestDTO1.email()))
                .willReturn(Optional.of(studentUser1));
        given(studentService.saveStudentUser(studentRequestDTO1))
                .willThrow(new UserAlreadyExistsException("User with email: " + studentRequestDTO1.email() + " already exists"));

        mockMvc.perform(post("/api/student/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(studentRequestDTO1)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").value("User with email: " + studentRequestDTO1.email() + " already exists"));
    }
    @Test
    @DisplayName("Student get enrolled course IDs list by ID successfully")
    public void studentGetCourseByIDSuccessfullyTest() throws Exception {
        String id = "10000";
        Set<String> courseIdList = Set.of("courseId1", "courseId2");
        studentUser1.setEnrolledCoursesId(courseIdList);
        given(userRepository.findById(id)).willReturn(Optional.of(studentUser1));
        given(studentService.getEnrolledCourseIDByStudentID(id)).willReturn(studentUser1.getEnrolledCoursesId());

        mockMvc.perform(get("/api/student/courses/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasItems("courseId1", "courseId2")));
    }
    @Test
    @DisplayName("Student get enrolled course IDs list throws User Not Found Exception")
    public void studentGetCourseByIDThrowsUserNotFoundExceptionTest() throws Exception {
        String id = "nonExistingID";

        given(studentService.getEnrolledCourseIDByStudentID(id)).willThrow(new UserNotFoundException("User with ID: " + id + " does not exist or is not a Student"));

        mockMvc.perform(get("/api/student/courses/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
    @Test
    @DisplayName("Add enrolled course ID by Student ID successfully")
    public void addEnrolledCourseByIDByProfessorIDTest() throws Exception {
        String id = "10000";
        String courseToAdd = "courseId3";
        String responseMessage = "Course ID added successfully to students. ";
        given(studentService.addEnrolledCourseIDByStudentID(id, courseToAdd)).willReturn(responseMessage);

        mockMvc.perform(post("/api/student/course/add/{id}", id)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("courseID",courseToAdd))
                .andExpect(status().isOk())
                .andExpect(content().string(responseMessage));
    }
    @Test
    @DisplayName("Add enrolled course ID by Student ID throws User Not Found Exception")
    public void addEnrolledCourseByIDByProfessorIDThrowsUserNotFoundExceptionTest() throws Exception {
        String id = "10000";
        String courseToAdd = "courseId3";
        String responseMessage = "Course ID added successfully to students. ";
        given(studentService.addEnrolledCourseIDByStudentID(id, courseToAdd)).willReturn(responseMessage);

        mockMvc.perform(post("/api/student/course/add/{id}", id)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("courseID",courseToAdd))
                .andExpect(status().isOk())
                .andExpect(content().string(responseMessage));
    }
    @Test
    @DisplayName("Delete course ID by student ID successfully")
    public void deleteCourseIdByStudentIdSuccessfully() throws Exception {
        String id = "10000";
        String courseToDelete = "courseId1";
        String response = "Course ID removed successfully from user with ID: " + id;

        given(studentService.removeEnrolledCourseIDByStudentID(id, courseToDelete)).willReturn(response);

        mockMvc.perform(delete("/api/student/courses/{id}", id)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("courseId", courseToDelete))
                .andExpect(status().isOk())
                .andExpect(content().string(response));
    }
}
