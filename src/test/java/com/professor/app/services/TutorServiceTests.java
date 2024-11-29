package com.professor.app.services;

import com.professor.app.dto.users.TutorRequestDTO;
import com.professor.app.dto.users.UserResponseDTO;
import com.professor.app.entities.Tutor;
import com.professor.app.exceptions.UserAlreadyExistsException;
import com.professor.app.exceptions.UserNotFoundException;
import com.professor.app.mapper.TutorMapper;
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
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class TutorServiceTests {

    TutorRequestDTO tutorRequestDTO;
    UserResponseDTO userResponseDTO;
    Tutor tutorUser1;
    Tutor tutorUser2;
    String studentId1;
    String studentId2;
    String studentId3;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private TutorService tutorService;

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
    @DisplayName("Save tutor user successfully")
    public void saveTutorUserSuccessfully() {
        String email = "campeon10@gmail.com";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(userRepository.save(tutorUser1)).thenReturn(tutorUser1);

        try (MockedStatic<TutorMapper> tutorMapperMockedStatic = mockStatic(TutorMapper.class); MockedStatic<UserMapper> userMapperMockedStatic = mockStatic(UserMapper.class)) {
            tutorMapperMockedStatic.when(() -> TutorMapper.toTutor(tutorRequestDTO)).thenReturn(tutorUser1);
            userMapperMockedStatic.when(() -> UserMapper.toUserResponseDTO(tutorUser1)).thenReturn(userResponseDTO);

            UserResponseDTO result = tutorService.saveTutorUser(tutorRequestDTO);

            assertNotNull(result);
            assertEquals("10000", result.id());
            assertEquals("Leonel", result.name());
            assertEquals("Messi", result.surname());
            assertEquals("campeon10@gmail.com", result.email());
            assertEquals(Role.TUTOR, result.role());
        }
    }
    @Test
    @DisplayName("Save tutor throws User Already Exists Exception")
    public void saveTutorThrowsUserAlreadyExistsException() {
        String email = "campeon10@gmail.com";

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(tutorUser1));

        UserAlreadyExistsException exception = assertThrows(UserAlreadyExistsException.class, () -> tutorService.saveTutorUser(tutorRequestDTO));

        assertEquals("User with email: " + email + " already exists", exception.getMessage());
    }
    @Test
    @DisplayName("Get students by tutor ID returns a list successfully")
    public void getStudentsIdListByTutorIdSuccessfullyTest() {
        String tutorId = "1000";

        when(userRepository.findById(tutorId)).thenReturn(Optional.of(tutorUser1));

        Set<String> result = tutorService.getStudentsIDsByTutorId(tutorId);

        assertNotNull(result);
        assertEquals(3, result.size());
        assertTrue(result.contains("studentId1"));
        assertTrue(result.contains("studentId2"));
        assertTrue(result.contains("studentId3"));
    }
    @Test
    @DisplayName("Get students by tutor ID throws User Not Found Exception")
    public void getStudentsIdListByTutorIdThrowsUserNotFoundExceptionTest() {
        String falseTutorId = "1000";

        when(userRepository.findById(falseTutorId)).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> tutorService.getStudentsIDsByTutorId(falseTutorId));

        assertEquals("User with ID: " + falseTutorId + " not found or is not a Tutor", exception.getMessage());

    }
    @Test
    @DisplayName("Add student ID to a Professor successfully")
    public void addStudentIDToProfessorSuccessfullyTest() {
        String id = "10000";
        String studentToAdd = "studentId5";

        when(userRepository.findById(id)).thenReturn(Optional.of(tutorUser1));
        when(userRepository.save(tutorUser1)).thenReturn(tutorUser1);

        String result = tutorService.addStudentIDByTutorID(id, studentToAdd);

        assertEquals("Student ID added successfully to user with ID: " + id, result);
        assertNotNull(tutorUser1.getTutoredStudentsId());
        assertTrue(tutorUser1.getTutoredStudentsId().contains(studentToAdd));
        verify(userRepository, times(1)).save(tutorUser1);
    }
    @Test
    @DisplayName("Add student ID to a Tutor throws User Not Found Exception")
    public void addStudentIDToTutorThrowsUserNotFoundTest() {
        String id = "90000";
        String studentToAdd = "studentId5";

        when(userRepository.findById(id)).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> tutorService.addStudentIDByTutorID(id, studentToAdd));

        assertEquals("User with ID: " + id + " not found or is not a Tutor", exception.getMessage());
    }
    @Test
    @DisplayName("Add a list of Students ID to a Tutor successfully")
    public void addListOfStudentsIDsToATutorSuccessfullyTest() {
        String id = "10000";
        List<String> listToAdd = List.of("studentId4", "studentId5", "studentId6");

        when(userRepository.findById(id)).thenReturn(Optional.of(tutorUser1));

        String result = tutorService.addMultipleStudentsIDByTutorID(id,listToAdd);

        assertEquals("Student IDs added successfully to user with ID: " + id, result);
        assertTrue(tutorUser1.getTutoredStudentsId().contains("studentId4"));
        assertTrue(tutorUser1.getTutoredStudentsId().contains("studentId5"));
        assertTrue(tutorUser1.getTutoredStudentsId().contains("studentId6"));
    }
    @Test
    @DisplayName("Add a list of Students ID to a Tutor throws User Not Found Exception")
    public void addListOfStudentsIDsToATutorThrowsUserNotFoundExceptionTest() {
        String id = "10000";
        List<String> listToAdd = List.of("studentId4", "studentId5", "studentId6");

        when(userRepository.findById(id)).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> tutorService.addMultipleStudentsIDByTutorID(id, listToAdd));

        assertEquals("User with ID: "+ id +" not found or is not a Tutor", exception.getMessage());
        assertFalse(tutorUser1.getTutoredStudentsId().contains("studentId4"));
        assertFalse(tutorUser1.getTutoredStudentsId().contains("studentId5"));
        assertFalse(tutorUser1.getTutoredStudentsId().contains("studentId6"));
    }
    @Test
    @DisplayName("Add a list of Students ID to a Tutor throws Illegal Argument Exception")
    public void addListOfStudentsIDsToATutorThrowsIllegalArgumentFoundExceptionTest() {
        String id = "10000";
        List<String> listToAdd = List.of();

        when(userRepository.findById(id)).thenReturn(Optional.of(tutorUser1));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> tutorService.addMultipleStudentsIDByTutorID(id, listToAdd));

        assertEquals("The list of student IDs cannot be null or empty.", exception.getMessage());

    }
    @Test
    @DisplayName("Add a list of Students ID empty to a Tutor throws Illegal Argument Exception")
    public void addListOfStudentsIDsEmptyToATutorThrowsIllegalArgumentFoundExceptionTest() {
        String id = "10000";
        List<String> listToAdd = List.of();
        tutorUser1.setTutoredStudentsId(Set.of());
        when(userRepository.findById(id)).thenReturn(Optional.of(tutorUser1));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> tutorService.addMultipleStudentsIDByTutorID(id, listToAdd));

        assertEquals("The list of student IDs cannot be null or empty.", exception.getMessage());

    }
    @Test
    @DisplayName("Delete student ID by Tutor ID successfully")
    public void deleteStudentIDByTutorIdSuccessfully() {
        String id = "10000";
        String studentIdToDelete = "studentId3";

        when(userRepository.findById(id)).thenReturn(Optional.ofNullable(tutorUser1));
        when(userRepository.save(tutorUser1)).thenReturn(tutorUser1);

        String result = tutorService.deleteStudentIDByTutorId(id,studentIdToDelete);

        assertEquals("Student successfully removed to user with ID: " + id, result);
        assertFalse(tutorUser1.getTutoredStudentsId().contains(studentIdToDelete));
    }
    @Test
    @DisplayName("Delete student ID by Tutor ID throws User Not Found Exception")
    public void deleteStudentIDByTutorIdThrowsUserNotFoundException() {
        String id = "10000";
        String studentIdToDelete = "studentId3";

        when(userRepository.findById(id)).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> tutorService.addStudentIDByTutorID(id, studentIdToDelete));

        assertEquals("User with ID: " + id + " not found or is not a Tutor", exception.getMessage());

    }
    @Test
    @DisplayName("Delete student ID by Tutor ID throws User Not Found Exception")
    public void deleteStudentIDByTutorIdThrowsIllegalArgumentException() {
        String id = "10000";
        String studentIdToDelete = "studentId3";
        tutorUser1.setTutoredStudentsId(null);
        when(userRepository.findById(id)).thenReturn(Optional.of(tutorUser1));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> tutorService.deleteStudentIDByTutorId(id, studentIdToDelete));

        assertEquals("Student ID not found for user with ID: " + id, exception.getMessage());
    }
    @Test
    @DisplayName("Delete student ID non existing by Tutor ID throws User Not Found Exception")
    public void deleteStudentIDNonExistingByTutorIdThrowsIllegalArgumentException() {
        String id = "10000";
        String studentIdToDelete = "studentId3";

        tutorUser1.setTutoredStudentsId(Set.of("studentId1", "studentId2"));
        when(userRepository.findById(id)).thenReturn(Optional.of(tutorUser1));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> tutorService.deleteStudentIDByTutorId(id, studentIdToDelete));

        assertEquals("Student ID not found for user with ID: " + id, exception.getMessage());
    }
    @Test
    @DisplayName("Delete Students ID List by Tutor ID - Success")
    public void deleteStudentsIDListByTutorID_Success() {
        String tutorId = tutorUser1.getId();
        Set<String> studentsToDelete = Set.of(studentId1, studentId2);

        when(userRepository.findById(tutorId)).thenReturn(Optional.of(tutorUser1));
        when(userRepository.save(tutorUser1)).thenReturn(tutorUser1);

        String result = tutorService.deleteStudentsIDListByTutorID(tutorId, studentsToDelete);

        assertEquals("Student IDs successfully deleted from user with ID: " + tutorId, result);
        assertFalse(tutorUser1.getTutoredStudentsId().containsAll(studentsToDelete));
        verify(userRepository, times(1)).save(tutorUser1);
    }

    @Test
    @DisplayName("Delete Students ID List by Tutor ID - Some IDs Not Found")
    public void deleteStudentsIDListByTutorID_SomeIDsNotFound() {
        String tutorId = tutorUser1.getId();
        Set<String> studentsToDelete = Set.of(studentId1, "nonExistentStudentId");

        when(userRepository.findById(tutorId)).thenReturn(Optional.of(tutorUser1));
        when(userRepository.save(tutorUser1)).thenReturn(tutorUser1);

        String result = tutorService.deleteStudentsIDListByTutorID(tutorId, studentsToDelete);

        assertEquals("Student IDs successfully deleted from user with ID: " + tutorId, result);
        assertFalse(tutorUser1.getTutoredStudentsId().contains(studentId1));
        verify(userRepository, times(1)).save(tutorUser1);
    }

    @Test
    @DisplayName("Delete Students ID List by Tutor ID - No Students Exist")
    public void deleteStudentsIDListByTutorID_NoStudentsExist() {
        String tutorId = tutorUser1.getId();
        tutorUser1.setTutoredStudentsId(new HashSet<>());

        when(userRepository.findById(tutorId)).thenReturn(Optional.of(tutorUser1));

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> tutorService.deleteStudentsIDListByTutorID(tutorId, Set.of(studentId1, studentId2)));

        assertEquals("No student IDs exist for user with ID: " + tutorId, exception.getMessage());
    }

    @Test
    @DisplayName("Delete Students ID List by Tutor ID - None of the Provided IDs Exist")
    public void deleteStudentsIDListByTutorID_NoneProvidedIDsExist() {
        String tutorId = tutorUser1.getId();
        Set<String> studentsToDelete = Set.of("nonExistentStudentId1", "nonExistentStudentId2");

        when(userRepository.findById(tutorId)).thenReturn(Optional.of(tutorUser1));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> tutorService.deleteStudentsIDListByTutorID(tutorId, studentsToDelete));

        assertEquals("None of the provided student IDs exist for user with ID: " + tutorId, exception.getMessage());
    }


}

