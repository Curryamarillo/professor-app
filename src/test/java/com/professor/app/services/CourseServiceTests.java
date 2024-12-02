package com.professor.app.services;

import com.professor.app.dto.courses.CourseRequestDTO;
import com.professor.app.dto.users.UserResponseDTO;
import com.professor.app.entities.Course;
import com.professor.app.repositories.CourseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class CourseServiceTests {
    @Mock
    private CourseRepository courseRepository;

    @InjectMocks
    private CourseService courseService;

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
    @DisplayName("Create course successfully")
    public void createCourseSuccessfully() {
        String code = "course-code-4";
        when(courseRepository.existsByCode(code)).thenReturn(false);
        when(courseRepository.save(any())).thenReturn(course1);

        String userResponseDTO = courseService.createCourse(courseRequestDTO1);

        ArgumentCaptor<Course> courseCaptor = ArgumentCaptor.forClass(Course.class);
        assertNotNull(userResponseDTO);
        verify(courseRepository).save(courseCaptor.capture());
        Course capturedCourse = courseCaptor.getValue();
        assertEquals(courseRequestDTO1.code(), capturedCourse.getCode());
        assertEquals(courseRequestDTO1.name(), capturedCourse.getName());
        assertEquals(courseRequestDTO1.comments(), capturedCourse.getComments());
        assertEquals(courseRequestDTO1.studentListId(), capturedCourse.getStudentsId());

    }
    @Test
    @DisplayName("Create Course throws Illegal Argument Exception")
    public void createCourseThrowsIllegalArgumentExceptionTest() {
        when(courseRepository.existsByCode(courseRequestDTO1.code())).thenReturn(true);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> courseService.createCourse(courseRequestDTO1));
        assertEquals("A course with code " + courseRequestDTO1.code() + " already exists", exception.getMessage());
        verify(courseRepository, never()).save(any(Course.class));
    }

    @Test
    @DisplayName("Should initialize empty studentsId set when student IDs list is null")
    public void shouldInitializeEmptyStudentsIdSetWhenNull() {
        String code = courseRequestDTO2.code();
        when(courseRepository.existsByCode(code)).thenReturn(false);

        ArgumentCaptor<Course> courseCaptor = ArgumentCaptor.forClass(Course.class);
        when(courseRepository.save(any(Course.class))).thenReturn(course1);

        String result = courseService.createCourse(courseRequestDTO2);

        assertEquals("Course added successfully with ID: " + course1.getId(), result);

        verify(courseRepository).save(courseCaptor.capture());
        Course capturedCourse = courseCaptor.getValue();

        assertNotNull(capturedCourse.getStudentsId(), "studentsId should not be null");
        assertTrue(capturedCourse.getStudentsId().isEmpty(), "studentsId should be an empty set");

        assertEquals(courseRequestDTO2.code(), capturedCourse.getCode());
        assertEquals(courseRequestDTO2.name(), capturedCourse.getName());
        assertEquals(courseRequestDTO2.comments(), capturedCourse.getComments());
    }
    @Test
    @DisplayName("Get a list of all courses successfully")
    public void getAllCoursesList() {
        when(courseRepository.findAll()).thenReturn(List.of(course1,course2, course3));

        List<Course> listOfCourses = courseService.findAllCourses();

        assertNotNull(listOfCourses);
        assertEquals(List.of(course1,course2, course3), listOfCourses);
        verify(courseRepository, times(1)).findAll();
    }
    @Test
    @DisplayName("Get courses by ID")
    public void getCoursesByIdSuccessfully() {
        String id = "courseId1";
        when(courseRepository.findById(id)).thenReturn(Optional.of(course1));

        Course course = courseService.getCourseById(id);

        System.out.println(course);
        assertNotNull(course);
        assertEquals(course1.getName(), course.getName());
        assertEquals(course1.getCode(), course.getCode());
        assertEquals(course1.getComments(), course.getComments());
        assertEquals(course1.getStudentsId(), course.getStudentsId());
    }
    @Test
    @DisplayName("Get courses by code")
    public void getCoursesByICodeSuccessfully() {
        String code = "course-code-1";
        when(courseRepository.findByCode(code)).thenReturn(Optional.of(course1));

        Course course = courseService.getCourseByCode(code);

        assertNotNull(course);
        assertEquals(course1.getName(), course.getName());
        assertEquals(course1.getCode(), course.getCode());
        assertEquals(course1.getComments(), course.getComments());
        assertEquals(course1.getStudentsId(), course.getStudentsId());
    }
    @Test
    @DisplayName("Get student list by course ID successfully")
    public void getStudentListByCourseUDSuccessfullyTest() {
        String id = "courseId1";
        Set<String> listStudentID = Set.of(studentId1, studentId2, studentId3);
        course1.getStudentsId().addAll(listStudentID);
        when(courseRepository.findById(id)).thenReturn(Optional.of(course1));

        Set<String> courseList = courseService.getStudentListId(id);

        assertNotNull(courseList);
        assertEquals(course1.getStudentsId(), courseList);
    }
    @Test
    @DisplayName("Update course by ID successfully")
    public void updateCourseByIDSuccessfully() {
        String courseId = "courseId1";
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course1));

        String result = courseService.updateCourseByID(courseId, "new-code", "new-name", "new-comments");

        assertEquals("Course with ID: " + courseId + " updated successfully", result);
        assertEquals("new-code", course1.getCode());
        assertEquals("new-name", course1.getName());
        assertEquals("new-comments", course1.getComments());
        verify(courseRepository).save(course1);
    }
    @Test
    @DisplayName("Add student ID to course successfully")
    public void addStudentIDToCourseSuccessfully() {
        String courseId = "courseId1";
        String studentId = "newStudentId";
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course1));

        String result = courseService.addStudentIDToCourse(courseId, studentId);

        assertEquals("Student added successfully to course ID: " + courseId, result);
        assertTrue(course1.getStudentsId().contains(studentId));
        verify(courseRepository, never()).save(any(Course.class));
    }
    @Test
    @DisplayName("Update student ID in course successfully")
    public void updateStudentIDToCourseSuccessfully() {
        String courseId = "courseId1";
        String oldStudentId = "studentId1";
        String newStudentId = "studentId2";
        course1.getStudentsId().add(oldStudentId);
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course1));

        String result = courseService.updateStudentIDToCourse(courseId, oldStudentId, newStudentId);

        assertEquals("Student updated successfully al course with ID: " + courseId, result);
        assertFalse(course1.getStudentsId().contains(oldStudentId));
        assertTrue(course1.getStudentsId().contains(newStudentId));
        verify(courseRepository).save(course1);
    }
    @Test
    @DisplayName("Delete student ID from course successfully")
    public void deleteStudentIDToCourseByIDSuccessfully() {
        // Arrange
        String courseId = "courseId1";
        String studentId = "studentId1";
        course1.getStudentsId().add(studentId);
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course1));

        // Act
        String result = courseService.deleteStudentIDToCourseByID(courseId, studentId);

        // Assert
        assertEquals("Student removed successfully", result);
        assertFalse(course1.getStudentsId().contains(studentId));
        verify(courseRepository).save(course1);
    }

    @Test
    @DisplayName("Delete student ID throws exception when student not found")
    public void deleteStudentIDToCourseByIDThrowsException() {
        String courseId = "courseId1";
        String studentId = "nonExistingStudentId";
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course1));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> courseService.deleteStudentIDToCourseByID(courseId, studentId));
        assertEquals("Course does not contain Student ID: " + courseId, exception.getMessage());
        verify(courseRepository, never()).save(any(Course.class));
    }
    @Test
    @DisplayName("Delete all students from course successfully")
    public void deleteAllStudentsFromCourseSuccessfully() {
        String courseId = "courseId1";
        course1.getStudentsId().add(studentId1);
        course1.getStudentsId().add(studentId2);
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course1));

        String result = courseService.deleteAllStudentsIdFromACourse(courseId);

        assertEquals("All students removed successfully", result);
        assertTrue(course1.getStudentsId().isEmpty());
        verify(courseRepository).save(course1);
    }






}
