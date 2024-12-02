package com.professor.app.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.professor.app.dto.courses.CourseRequestDTO;
import com.professor.app.entities.Course;
import com.professor.app.services.CourseService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.hamcrest.Matchers.hasItems;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class CourseControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CourseService courseService;

    @Autowired
    private ObjectMapper objectMapper;

    Course course1;
    Course course2;
    Course course3;

    String studentId1;
    String studentId2;
    String studentId3;

    CourseRequestDTO courseRequestDTO1;
    CourseRequestDTO courseRequestDTO2;

    @BeforeEach
    void setUp() {
        course1 = Course.builder()
                .id("courseId1")
                .code("course-code-1")
                .name("course-name-1")
                .comments("Course comments one")
                .studentsId(new HashSet<>())
                .build();
        course2 = Course.builder()
                .id("courseId2")
                .code("course-code-2")
                .name("course-name-2")
                .comments("Course comments two")
                .studentsId(new HashSet<>())
                .build();
        course3 = Course.builder()
                .id("courseId3")
                .code("course-code-3")
                .name("course-name-3")
                .comments("Course comments three")
                .studentsId(new HashSet<>())
                .build();

        studentId1 = "studentId1";
        studentId2 = "studentId2";
        studentId3 = "studentId3";

        courseRequestDTO1 = new CourseRequestDTO("course-code-4", "course-name-4", "Course comments 4", Set.of(studentId1, studentId2, studentId3));
        courseRequestDTO2 = new CourseRequestDTO("course-code-5", "course-name-5", "Course comments 5", null);

    }

    @Test
    @DisplayName("Course created successfully")
    public void courseCreatedSuccessfullyTest() throws Exception {
        String response = "Course added successfully with ID: " + course1.getId();
        given(courseService.createCourse(courseRequestDTO1)).willReturn(response);

        mockMvc.perform(post("/api/course/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(courseRequestDTO1)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$").value(response));
    }
    @Test
    @DisplayName("Get a list of all courses")
    public void getAListOfAllCourses() throws Exception {
        List<Course> listOfCourses = List.of(course1, course2, course3);

        given(courseService.findAllCourses()).willReturn(listOfCourses);

        mockMvc.perform(get("/api/course")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(listOfCourses.size()))
                .andExpect(jsonPath("$[0].id").value(course1.getId()))
                .andExpect(jsonPath("$[0].name").value(course1.getName()))
                .andExpect(jsonPath("$[1].id").value(course2.getId()))
                .andExpect(jsonPath("$[1].name").value(course2.getName()))
                .andExpect(jsonPath("$[2].id").value(course3.getId()))
                .andExpect(jsonPath("$[2].name").value(course3.getName()));
    }

    @Test
    @DisplayName("Get a course by ID successfully")
    public void getACourseByID() throws Exception {
        String id = "courseId1";

        given(courseService.getCourseById(id)).willReturn(course1);

        mockMvc.perform(get("/api/course/get/{id}", id)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("id", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(course1.getId()))
                .andExpect(jsonPath("$.name").value(course1.getName()))
                .andExpect(jsonPath("$.comments").value(course1.getComments()))
                .andExpect(jsonPath("$.studentsId").isEmpty());

    }
    @Test
    @DisplayName("Get a course by code successfully")
    public void getACourseByCode() throws Exception {
        String code = "course-code-1";

        given(courseService.getCourseByCode(code)).willReturn(course1);

        mockMvc.perform(get("/api/course/get-code/{code}", code)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("id", code))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(course1.getId()))
                .andExpect(jsonPath("$.name").value(course1.getName()))
                .andExpect(jsonPath("$.comments").value(course1.getComments()))
                .andExpect(jsonPath("$.studentsId").isEmpty());

    }
    @Test
    @DisplayName("Get student list by course ID")
    public void getStudentListByCourseID() throws Exception {
        String courseId = "courseId1";
        Set<String> listOfStudents = Set.of(studentId1, studentId2, studentId3);
        given(courseService.getStudentListId(courseId)).willReturn(listOfStudents);

        mockMvc.perform(get("/api/course/student-at/{id}", courseId)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(listOfStudents.size()))
                .andExpect(jsonPath("$").value(Matchers.containsInAnyOrder(studentId1,studentId2,studentId3)));

    }
    @Test
    @DisplayName("Update course values by Course ID successfully")
    public void updateCourseByIDSuccessfully() throws Exception {
        String id = "courseId1";
        String code = "new-code";
        String name = "new-name";
        String comments = "new-comments";
        String response = "Course with ID: " + id + " updated successfully";
        given(courseService.updateCourseByID(id, code, name, comments)).willReturn(response);

        mockMvc.perform(put("/api/course/update/{id}", id)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("name", name)
                .param("code", code)
                .param("comments", comments))
                .andExpect(status().isOk())
                .andExpect(content().string(response));
    }
    @Test
    @DisplayName("Update a student ID at a course")
    public void updateStudentIDAtACourseTest() throws Exception {
        String id = "courseId1";
        String oldStudentId = "studentId1";
        String newStudentId = "newStudentId1";
        String response = "Student updated successfully al course with ID: " + id;
        given(courseService.updateStudentIDToCourse(id,oldStudentId, newStudentId)).willReturn("Student updated successfully al course with ID: " + id);

        mockMvc.perform(patch("/api/course/update-list/{id}", id)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("oldStudentID", oldStudentId)
                .param("newStudentID", newStudentId))
                .andExpect(status().isOk())
                .andExpect(content().string(response));

    }
    @Test
    @DisplayName("Add a student ID to a course successfully")
    public void addStudentIdToACourseTest() throws Exception {
        String courseId = "courseId1";
        String studentId = "studentId1";
        String response = "Student with ID: " + studentId + " added to course with ID: " + courseId;

        given(courseService.addStudentIDToCourse(courseId, studentId)).willReturn(response);

        mockMvc.perform(post("/api/course/add-student/{id}", courseId)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("studentID", studentId))
                .andExpect(status().isOk())
                .andExpect(content().string(response));
    }
    @Test
    @DisplayName("Delete a student ID from a course successfully")
    public void deleteStudentByIDAtACourseIDTest() throws Exception {
        String courseId = "courseId1";
        String studentId = "studentId1";
        String response = "Student with ID: " + studentId + " deleted from course with ID: " + courseId;

        given(courseService.deleteStudentIDToCourseByID(courseId, studentId)).willReturn(response);

        mockMvc.perform(delete("/api/course/delete-student/{id}", courseId)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("studentId", studentId))
                .andExpect(status().isOk())
                .andExpect(content().string(response));
    }
    @Test
    @DisplayName("Delete all students from a course successfully")
    public void deleteAllStudentsOfACourseTest() throws Exception {
        String courseId = "courseId1";
        String response = "All students deleted from course with ID: " + courseId;

        given(courseService.deleteAllStudentsIdFromACourse(courseId)).willReturn(response);

        mockMvc.perform(delete("/api/course/delete-all-students/{id}", courseId)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(content().string(response));
    }



}
