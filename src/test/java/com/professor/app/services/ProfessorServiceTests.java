package com.professor.app.services;

import com.professor.app.dto.users.ProfessorRequestDTO;
import com.professor.app.dto.users.UserResponseDTO;
import com.professor.app.entities.Professor;
import com.professor.app.exceptions.UserAlreadyExistsException;
import com.professor.app.exceptions.UserNotFoundException;
import com.professor.app.mapper.ProfessorMapper;
import com.professor.app.mapper.UserMapper;
import com.professor.app.repositories.UserRepository;
import com.professor.app.roles.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProfessorServiceTests {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ProfessorService professorService;

    ProfessorRequestDTO professorRequestDTO1;
    UserResponseDTO userResponseDTO1;

    Professor professorUser1;
    Professor professorUser2;

    String courseId1;
    String courseId2;

    String studentId1;
    String studentId2;
    String studentId3;

    @BeforeEach
    void setUp() {
        courseId1 = "course1";
        courseId2 = "course2";

        studentId1 = "studentId1";
        studentId2 = "studentId2";
        studentId3 = "studentId3";

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
                .studentsIds(new HashSet<>(Set.of(studentId1, studentId2, studentId3)))
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
                .studentsIds(new HashSet<>(Set.of(studentId1, studentId2, studentId3)))
                .build();

        professorRequestDTO1 = new ProfessorRequestDTO("Leonel", "Messi Cuccitini", "campeon10@gmail.com", "password1", "10001", Set.of(courseId1, courseId2), Set.of(studentId1, studentId2));

         userResponseDTO1 = new UserResponseDTO("10000", "Leonel", "Messi", "campeon10@gmail.com", "10000", Role.PROFESSOR);
    }
    @Test
    @DisplayName("Save professor user successfully")
    public void saveProfessorUserSuccessfully() {
        String email = "campeon10@gmail.com";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(userRepository.save(professorUser1)).thenReturn(professorUser1);

        try (MockedStatic<ProfessorMapper> professorMapperMockedStatic = mockStatic(ProfessorMapper.class);
             MockedStatic<UserMapper> userMapperMockedStatic = mockStatic(UserMapper.class)) {

            professorMapperMockedStatic.when(() -> ProfessorMapper.toProfessor(professorRequestDTO1)).thenReturn(professorUser1);
            userMapperMockedStatic.when(() -> UserMapper.toUserResponseDTO(professorUser1)).thenReturn(userResponseDTO1);

            UserResponseDTO result = professorService.saveProfessorUser(professorRequestDTO1);

            assertNotNull(result);
            assertEquals("10000", result.id());
            assertEquals("Leonel", result.name());
            assertEquals("Messi", result.surname());
            assertEquals("campeon10@gmail.com", result.email());
            assertEquals(Role.PROFESSOR, result.role());
        }
    }
    @Test
    @DisplayName("Save professor throws User Already Exists Exception")
    public void saveProfessorThrowsUserAlreadyExistsException() {
        String email = "campeon10@gmail.com";

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(professorUser1));

        UserAlreadyExistsException exception = assertThrows(UserAlreadyExistsException.class, () -> professorService.saveProfessorUser(professorRequestDTO1));

        assertEquals("User with email: " + email + " already exists", exception.getMessage());
    }

    @Test
    @DisplayName("Get courses by ID returns a list successfully")
    public void getCourseIdListByIdSuccessfullyTest() {
        String professorId = "1000";

        when(userRepository.findById(professorId)).thenReturn(Optional.of(professorUser1));

        Set<String> result = professorService.getCourseIdListById(professorId);

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

        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> professorService.getCourseIdListById(falseId));

    assertEquals("User with ID: " + falseId + " does not exist or is not a Professor", exception.getMessage());
    }
    @Test
    @DisplayName("Add course ID to a Professor successfully")
    public void addCourseIDToProfessorSuccessfully() {
        String id = "10000";
        String courseToAdd = "course5";

        when(userRepository.findById(id)).thenReturn(Optional.of(professorUser1));
        when(userRepository.save(professorUser1)).thenReturn(professorUser1);

        String result = professorService.addCourseIdByProfessorId(id, courseToAdd);

        assertEquals("Course ID added successfully to user with ID: " + id, result);
        assertNotNull(professorUser1.getCourseIds());
        assertTrue(professorUser1.getCourseIds().contains(courseToAdd));
        assertTrue(professorUser1.getCourseIds().contains("course1"));
        assertTrue(professorUser1.getCourseIds().contains("course2"));
        assertEquals(3, professorUser1.getStudentsIds().size());
        assertTrue(professorUser1.getStudentsIds().contains("studentId1"));
        assertTrue(professorUser1.getStudentsIds().contains("studentId2"));
        verify(userRepository, times(1)).save(professorUser1);
        assertEquals(3, professorUser1.getCourseIds().size());
    }

    @Test
    @DisplayName("Add course ID throws User Not Found Exception")
    public void addCourseIDThrowsUserNotFoundExceptionTest() {
        String id = "10001";
        String courseId = "course5";

        when(userRepository.findById(id)).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> professorService.addCourseIdByProfessorId(id, courseId));

        assertEquals("User with ID: " + id + " does not exist or is not a Professor", exception.getMessage());
    }
    @Test
    @DisplayName("Remove course ID by ID successfully")
    public void removeCourseIDByUserIDSuccessfullyTest() {
        String id = "10000";
        String courseIDToRemove = "course2";

        when(userRepository.findById(id)).thenReturn(Optional.of(professorUser1));
        when(userRepository.save(professorUser1)).thenReturn(professorUser1);

        String result = professorService.removeCourseIdById(id, courseIDToRemove);

        assertEquals("Course ID removed successfully from user with ID: " + id, result);
        assertFalse(professorUser1.getCourseIds().contains(courseIDToRemove));
        verify(userRepository).findById(id);
    }
    @Test
    @DisplayName("Remove course ID by ID throws Illegal State Exception")
    public void removeCourseIdByUserIdThrowsIllegalStateException() {
        String id = "10000";
        String courseId = "courseId1";

        Professor professor = Professor.builder()
                .id("10000")
                .courseIds(null)
                .build();

        when(userRepository.findById(id)).thenReturn(Optional.of(professor));

        Exception exception = assertThrows(IllegalStateException.class,
                () -> professorService.removeCourseIdById(id, courseId));

        assertEquals("Course IDs not initialized for user with ID: " + id, exception.getMessage());
        verify(userRepository, never()).save(any());
    }
    @Test
    @DisplayName("Remove course ID by ID throws Illegal Argument Exception")
    public void removeCourseIdByUserIdThrowsIllegalArgumentException() {
        String id = "10000";
        String courseId = "course3";


        when(userRepository.findById(id)).thenReturn(Optional.of(professorUser1));

        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> professorService.removeCourseIdById(id, courseId));

        assertEquals("Course with ID: " + courseId + " not found", exception.getMessage());

    }
    @Test
    @DisplayName("Get student ID list successfully")
    public void getStudentIdListSuccessfully() {
        String id = "10000";

        when(userRepository.findById(id)).thenReturn(Optional.ofNullable(professorUser1));

        Set<String> studentIdList = professorService.getStudentIdList(id);

        assertEquals(3, studentIdList.size());
        assertTrue(studentIdList.contains("studentId1"));
        assertTrue(studentIdList.contains("studentId2"));
        assertEquals(professorUser1.getStudentsIds(), studentIdList);
        verify(userRepository, times(1)).findById(id);
    }
    @Test
    @DisplayName("Add Student ID by professor ID successfully")
    public void addStudentIdByProfessorIdSuccessfully() {
        String id = "1000";
        String studentId = "newStudentId";



        when(userRepository.findById(id)).thenReturn(Optional.of(professorUser1));
        when(userRepository.save(professorUser1)).thenReturn(professorUser1);

        String result = professorService.addStudentIdByProfessorId(id, studentId);

        assertEquals("Student ID added successfully to user with ID: " + id, result);
        assertNotNull(professorUser1.getStudentsIds());
        assertTrue(professorUser1.getStudentsIds().contains(studentId));
        assertEquals(4, professorUser1.getStudentsIds().size());
    }
    @Test
    @DisplayName("Add student ID by professor ID throws User Not Found Exception")
    public void addStudentIdByProfessorIdThrowsUserNotFoundException() {
        String id = "90000";
        String studentId = "falseId1";

        when(userRepository.findById(id)).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> professorService.addStudentIdByProfessorId(id, studentId));


    }
    @Test
    @DisplayName("Add a list of student ID successfully")
    public void addAListOfStudentIdsByProfessorIdSuccessfully() {
        String id = "10000";
        Set<String> listToAdd = Set.of("studentId3","studentId4","studentId5");

        when(userRepository.findById(id)).thenReturn(Optional.of(professorUser1));
        when(userRepository.save(professorUser1)).thenReturn(professorUser1);

        String result = professorService.addMultipleStudentsId(id, listToAdd);

        // this checks includes previous student IDs
        assertEquals("Student IDs added successfully to user with ID: " + id, result);
        assertTrue(professorUser1.getStudentsIds().contains("studentId4"));
        assertTrue(professorUser1.getStudentsIds().contains("studentId5"));
        assertTrue(professorUser1.getStudentsIds().contains("studentId1"));

        verify(userRepository, times(1)).findById(id);
        verify(userRepository, times(1)).save(professorUser1);
    }
    @Test
    void addMultipleStudentsId_ShouldThrowException_WhenStudentIdsIsNull() {
        String id = "10000";

        when(userRepository.findById(id)).thenReturn(Optional.ofNullable(professorUser1));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            professorService.addMultipleStudentsId(id, Collections.emptySet());
        });

        assertEquals("The set of student IDs cannot be null or empty.", exception.getMessage());
    }
    @Test
    @DisplayName("Add student id list empty throws Illegal Argument Exception")
    public void addStudentIDListEmptyThrowsIllegalArgumentExceptionTest() {
        String id = "10000";
        professorUser1.setStudentsIds(null);
        when(userRepository.findById(id)).thenReturn(Optional.of(professorUser1));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            professorService.addMultipleStudentsId(id, professorUser1.getStudentsIds());
        });

        assertEquals("The set of student IDs cannot be null or empty.", exception.getMessage());


    }
    @Test
    @DisplayName("Delete student by id with professor id successfully")
    public void deleteStudentByIdWithProfessorIdSuccessfullyTest() {
        String id = "10000";
        String studentToDelete = "studentId1";

        when(userRepository.findById(id)).thenReturn(Optional.ofNullable(professorUser1));
        when(userRepository.save(professorUser1)).thenReturn(professorUser1);

        String result = professorService.deleteStudentIdByProfessorId(id, studentToDelete);

        assertEquals("Student successfully deleted to user with ID: " + id, result);
        assertFalse(professorUser1.getStudentsIds().contains(studentToDelete));
        verify(userRepository).save(professorUser1);
        verify(userRepository).findById(id);
    }
    @Test
    @DisplayName("Delete student by id with professor id throws User Not Found Error")
    public void deleteStudentByIdWithProfessorThrowsUserNotFoundExceptionTest() {
        String id = "90000";
        String studentToDelete = "studentId1";

        when(userRepository.findById(id)).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> professorService.deleteStudentIdByProfessorId(id, studentToDelete)
        );

        assertEquals("User with ID: " + id + " does not exist or is not a Professor", exception.getMessage());
        verify(userRepository).findById(id);
    }

    @Test
    @DisplayName("Delete student ID is null throws Illegal Argument Exception")
    public void deleteStudentIDIsNullThrowsIllegalArgumentExceptionTest() {
        String id = "10000";
        professorUser1.setStudentsIds(null);
        when(userRepository.findById(id)).thenReturn(Optional.of(professorUser1));


        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> professorService.deleteStudentIdByProfessorId(id, null)
        );
        assertEquals("Student ID not found for user with ID: " + id, exception.getMessage());
        verify(userRepository).findById(id);
    }
    @Test
    @DisplayName("Delete student ID is null throws Illegal Argument Exception")
    public void deleteStudentIDNotContainsThrowsIllegalArgumentExceptionTest() {
        String id = "10000";
        String idToDelete = "studentId4";
        professorUser1.setStudentsIds(Set.of("studentId2", "studentId3"));
        when(userRepository.findById(id)).thenReturn(Optional.of(professorUser1));


        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> professorService.deleteStudentIdByProfessorId(id, idToDelete)
        );
        assertEquals("Student ID not found for user with ID: " + id, exception.getMessage());
        verify(userRepository).findById(id);
    }
    @Test
    @DisplayName("Delete by student ID list by professor ID successfully")
    public void deleteByIDListByProfessorIDSuccessfullyTest() {
        String id = "10000";
        String studentIDToDelete = "studentId2";

        when(userRepository.findById(id)).thenReturn(Optional.ofNullable(professorUser1));
        when(userRepository.save(professorUser1)).thenReturn(professorUser1);

        String result = professorService.deleteStudentIdByProfessorId(id, studentIDToDelete);

        assertEquals("Student successfully deleted to user with ID: " + id, result);
        assertFalse(professorUser1.getStudentsIds().contains(studentIDToDelete));
        verify(userRepository).findById(id);
        verify(userRepository).save(professorUser1);
    }
    @Test
    @DisplayName("Delete by student IDs list by professor successfully")
    public void deleteByStudentIDListByProfessorIdTest() {
        String id = "10000";
        Set<String> studentIDsListToDelete = Set.of("studentId2", "studentId3");

        when(userRepository.findById(id)).thenReturn(Optional.ofNullable(professorUser1));
        when(userRepository.save(professorUser1)).thenReturn(professorUser1);

        String result = professorService.deleteStudentsIdListByProfessorId(id, studentIDsListToDelete);

        assertEquals("All student IDs successfully deleted from user with ID: " + id, result);
        assertTrue(Collections.disjoint(professorUser1.getStudentsIds(), studentIDsListToDelete));

    }
    @Test
    @DisplayName("Delete by student IDs list by professor throws Illegal State Exception")
    public void deleteByStudentIDListByProfessorIdThrowsTest() {
        String id = "10000";
        Set<String> studentIDsListToDelete = Set.of("studentId2", "studentId3");
        professorUser1.setStudentsIds(Set.of());
        when(userRepository.findById(id)).thenReturn(Optional.ofNullable(professorUser1));


        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> professorService.deleteStudentsIdListByProfessorId(id, studentIDsListToDelete));

        assertEquals("No student IDs exist for user with ID: " + id, exception.getMessage());
    }
    @Test
    @DisplayName("Delete by student IDs list by professor throws Illegal Argument Exception")
    public void deleteByStudentIDsListByProfessorThrowsIllegalArgumentExceptionTest() {
        String id = "10000";
        Set<String> studentIDListToDelete = Set.of("nonExistingStudentId1","nonExistingStudentId2");
        when(userRepository.findById(id)).thenReturn(Optional.ofNullable(professorUser1));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> professorService.deleteStudentsIdListByProfessorId(id,studentIDListToDelete)
        );
        assertEquals("None of the provided student IDs exist for user with ID: " + id, exception.getMessage());

    }
    @Test
    @DisplayName("Delete by some student IDs list by professor successfully")
    public void deleteBySomeStudentIDsListByProfessorThrowsIllegalArgumentExceptionTest() {
        String id = "10000";
        Set<String> studentIDListToDelete = Set.of("studentId1","nonExistingStudentId2");
        when(userRepository.findById(id)).thenReturn(Optional.ofNullable(professorUser1));

        String result = professorService.deleteStudentsIdListByProfessorId(id, studentIDListToDelete);

        assertEquals("Some student IDs were not found: [nonExistingStudentId2]. Others were successfully deleted.", result);
        assertFalse(professorUser1.getStudentsIds().contains("studentId1"));
        assertTrue(professorUser1.getStudentsIds().contains("studentId2"));
        assertTrue(professorUser1.getStudentsIds().contains("studentId2"));
        assertFalse(professorUser1.getStudentsIds().contains("nonExistingStudentId2"));

    }

}
