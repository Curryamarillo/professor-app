package com.professor.app.services;

import com.professor.app.dto.users.StudentRequestDTO;
import com.professor.app.dto.users.UserResponseDTO;
import com.professor.app.entities.Professor;
import com.professor.app.entities.Student;
import com.professor.app.entities.User;
import com.professor.app.exceptions.UserAlreadyExistsException;
import com.professor.app.exceptions.UserNotFoundException;
import com.professor.app.mapper.StudentMapper;
import com.professor.app.mapper.UserMapper;
import com.professor.app.repositories.UserRepository;
import com.professor.app.roles.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class StudentServiceTests {

    StudentRequestDTO studentRequestDTO1;
    UserResponseDTO userResponseDTO1;

    Student studentUser1;
    Student studentUser2;
    Student studentUser3;

    String courseId1;
    String courseId2;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private StudentService studentService;

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
    @DisplayName("Save Student user successfully")
    public void saveStudentUserSuccessfully() {
        String email = "campeon10@gmail.com";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(userRepository.save(studentUser1)).thenReturn(studentUser1);

        try (MockedStatic<StudentMapper> studentMapperMockedStatic = mockStatic(StudentMapper.class); MockedStatic<UserMapper> userMapperMockedStatic = mockStatic(UserMapper.class)) {

            studentMapperMockedStatic.when(() -> StudentMapper.toStudent(studentRequestDTO1)).thenReturn(studentUser1);
            userMapperMockedStatic.when(() -> UserMapper.toUserResponseDTO(studentUser1)).thenReturn(userResponseDTO1);

            UserResponseDTO result = studentService.saveStudentUser(studentRequestDTO1);

            assertNotNull(result);
            assertEquals("Leonel", result.name());
            assertEquals("Messi", result.surname());
            assertEquals("campeon10@gmail.com", result.email());
            assertEquals(Role.STUDENT, result.role());
        }
    }
    @Test
    @DisplayName("Save Student throws User Already Exists Exception")
    public void saveStudentThrowsUserAlreadyExistsException() {
        String email = "campeon10@gmail.com";

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(studentUser1));

        UserAlreadyExistsException exception = assertThrows(UserAlreadyExistsException.class, () -> studentService.saveStudentUser(studentRequestDTO1));

        assertEquals("User with email: " + email + " already exists.", exception.getMessage());
    }
    @Test
    @DisplayName("Get courses by ID returns a list successfully")
    public void getCourseIdListByIdSuccessfullyTest() {
        String id = "1000";

        when(userRepository.findById(id)).thenReturn(Optional.of(studentUser1));

        Set<String> result = studentService.getEnrolledCourseIDByStudentID(id);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains("course1"));
        assertTrue(result.contains("course2"));
    }
    @Test
    @DisplayName("Get courses by Id returns an User Not Found Exception")
    public void getCoursesThrowsAnUserNotFoundException() {
        String falseId = "90000";

        when(userRepository.findById(falseId)).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> studentService.getEnrolledCourseIDByStudentID(falseId));

        assertEquals("User with ID: " + falseId + " does not exist or is not a Student", exception.getMessage());
    }
    @Test
    @DisplayName("Add enrolled course ID by Student ID successfully")
    public void addEnrolledCourseIDByStudentIDSuccessfullyTest(){
        String id = "10000";
        String courseToAdd = "courseID5";

        when(userRepository.findById(id)).thenReturn(Optional.of(studentUser1));

        String result = studentService.addEnrolledCourseIDByStudentID(id, courseToAdd);

        assertEquals("Course ID added successfully to user with ID: " + id, result);
        assertTrue(studentUser1.getEnrolledCoursesId().contains(courseToAdd));
    }
    @Test
    @DisplayName("Remove a course ID from a student successfully")
    public void removeEnrolledCourseIDByStudentIDSuccessfully() {
        String studentID = "10000";
        String courseToRemove = "course1";

        when(userRepository.findById(studentID)).thenReturn(Optional.of(studentUser1));

        String result = studentService.removeEnrolledCourseIDByStudentID(studentID, courseToRemove);

        assertEquals("Course ID removed successfully from user with ID: " + studentID, result);

        assertFalse(studentUser1.getEnrolledCoursesId().contains(courseToRemove));

        verify(userRepository, times(1)).save(studentUser1);
    }
    @Test
    @DisplayName("Throw IllegalArgumentException when course ID is not found for student")
    public void removeEnrolledCourseIDByStudentIDThrowsIllegalArgumentException() {
        String studentID = "10000";
        String courseToRemove = "course5";

        when(userRepository.findById(studentID)).thenReturn(Optional.of(studentUser1));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> studentService.removeEnrolledCourseIDByStudentID(studentID, courseToRemove));

        assertEquals("Course with ID: " + courseToRemove + " not found", exception.getMessage());

        verify(userRepository, never()).save(any());
    }
    @Test
    @DisplayName("Throw IllegalStateException when enrolledCoursesId is null")
    public void removeEnrolledCourseIDByStudentIDThrowsIllegalStateException() {
        String studentID = "10000";
        String courseToRemove = "course1";

        Student studentWithNullCourses = new Student();
        studentWithNullCourses.setEnrolledCoursesId(null);

        when(userRepository.findById(studentID)).thenReturn(Optional.of(studentWithNullCourses));

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> studentService.removeEnrolledCourseIDByStudentID(studentID, courseToRemove));

        assertEquals("Course IDs not initialized for user with ID: " + studentID, exception.getMessage());

        verify(userRepository, never()).save(any());
    }


}
