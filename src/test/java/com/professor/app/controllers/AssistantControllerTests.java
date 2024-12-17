package com.professor.app.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.professor.app.dto.users.AssistantRequestDTO;
import com.professor.app.dto.users.UserResponseDTO;
import com.professor.app.dto.users.UserUpdateDTO;
import com.professor.app.entities.Assistant;
import com.professor.app.exceptions.UserAlreadyExistsException;
import com.professor.app.exceptions.UserNotFoundException;
import com.professor.app.repositories.UserRepository;
import com.professor.app.services.AssistantService;
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
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.professor.app.roles.Role.ADMIN;
import static org.hamcrest.Matchers.hasItems;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
public class AssistantControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private AssistantService assistantService;

    @MockBean
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    UserResponseDTO userResponseDTO1;
    UserResponseDTO userResponseDTO2;
    Assistant assistantUser1;
    Assistant assistantUser2;

    AssistantRequestDTO assistantRequestDTO1;
    UserUpdateDTO assistantUpdateRequestDTO;

    @BeforeEach
    void setUp() {
        assistantUser1 = Assistant.builder()
                .id("10000")
                .name("Leonel")
                .surname("Messi")
                .email("campeon10@gmail.com")
                .dni("10000")
                .role(ADMIN)
                .password("password1")
                .createdAt(LocalDateTime.of(2024, 1, 1, 1, 0, 0))
                .modifiedAt(LocalDateTime.of(2024, 1, 1, 1, 1, 10))
                .build();

        assistantUser2 = Assistant.builder()
                .id("10001")
                .name("Fideo")
                .surname("Dimaria")
                .email("campeon11@gmail.com")
                .dni("10002")
                .role(ADMIN)
                .password("password2")
                .createdAt(LocalDateTime.of(2024, 1, 1, 1, 0, 0))
                .modifiedAt(LocalDateTime.of(2024, 1, 1, 1, 1, 10))

                .build();
        assistantRequestDTO1 = new AssistantRequestDTO(
                "Leonel",
                "Messi",
                "campeon10@gmail.com",
                "password1",
                "10000",
                List.of("course1", "course2"),
                Set.of("duty1", "duty2")
        );
        assistantUpdateRequestDTO = new UserUpdateDTO(
                "Lionel",
                "Messi Cuccitini",
                "campeónDeTodo",
                "10"
        );

        userResponseDTO1 = new UserResponseDTO("10000", "Leonel", "Messi", "campeon10@gmail.com", "10000", ADMIN);
        userResponseDTO2 = new UserResponseDTO("10001", "Fideo", "Dimaria", "campeon11@gmail.com", "10000", ADMIN);
    }

    @Test
    @DisplayName("Assistant created successfully")
    public void assistantCreatedSuccessfullyTest() throws Exception {
        given(assistantService.saveAssistant(assistantRequestDTO1)).willReturn(userResponseDTO1);

        mockMvc.perform(post("/api/assistant/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(assistantRequestDTO1)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(userResponseDTO1.id()))
                .andExpect(jsonPath("$.name").value(userResponseDTO1.name()))
                .andExpect(jsonPath("$.surname").value(userResponseDTO1.surname()))
                .andExpect(jsonPath("$.email").value(userResponseDTO1.email()))
                .andExpect(jsonPath("$.dni").value(userResponseDTO1.dni()))
                .andExpect(jsonPath("$.role").value("ADMIN"));

    }

    @Test
    @DisplayName("Assistant created not successfully throws exception")
    public void assistantCreatedThrowsUserNotCreatedExceptionTest() throws Exception {

        given(userRepository.findByEmail(assistantRequestDTO1.email()))
                .willReturn(Optional.of(assistantUser1));
        given(assistantService.saveAssistant(assistantRequestDTO1))
                .willThrow(new UserAlreadyExistsException("User with email " + assistantRequestDTO1.email() + " already exists"));

        mockMvc.perform(post("/api/assistant/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(assistantRequestDTO1)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").value("User with email " + assistantRequestDTO1.email() + " already exists"));
    }

    ///  duties tests
    @Test
    @DisplayName("Get duties successfully")
    public void getDutiesSuccessfullyTest() throws Exception {
        String id = "10000";
        Set<String> duties = Set.of("duty1", "duty2");
        assistantUser1.setDuties(duties);

        given(userRepository.findById(id)).willReturn(Optional.of(assistantUser1));
        given(assistantService.getDutiesById(id)).willReturn(duties);

        mockMvc.perform(get("/api/assistant/duties/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasItems("duty1", "duty2")));
    }

    @Test
    @DisplayName("Get duties by user Id throws User Not Found Exception")
    public void getDutiesThrowsUserNotFoundExceptionTest() throws Exception {
        String id = "90000";

        given(assistantService.getDutiesById(id)).willThrow(new UserNotFoundException("User with id: " + id + " not found"));

        mockMvc.perform(get("/api/assistant/duties/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("User with id: " + id + " not found"))
                .andExpect(jsonPath("timestamp").exists());
    }

    @Test
    @DisplayName("Add duty by id successfully")
    public void addDutyByIdSuccessfullyTest() throws Exception {
        String id = "10000";
        String newDuty = "New duty";

        String responseMessage = "Duty added successfully to id: " + id;

        given(assistantService.addDutyById(id, newDuty)).willReturn(responseMessage);

        mockMvc.perform(post("/api/assistant/duties/add/{id}", id)
                        .param("duty", newDuty)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(content().string(responseMessage));
    }

    @Test
    @DisplayName("Add duty by Id User Not Found")
    public void addDutyByIdThrowsUserNotFoundTest() throws Exception {
        String id = "90000";
        String newDuty = "New duty";

        given(assistantService.addDutyById(id, newDuty))
                .willThrow(new UserNotFoundException("User with id: " + id + " does not exist or is not an Assistant"));

        mockMvc.perform(post("/api/assistant/duties/add/{id}", id)
                        .param("duty", newDuty)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("User with id: " + id + " does not exist or is not an Assistant"));
    }

    @Test
    @DisplayName("Remove duty by id successfully")
    public void removeDutyByIdSuccessfully() throws Exception {
        String id = "10000";
        String dutyToDelete = "duty2";
        String responseMessage = "Duty removed successfully from user with id: " + id;

        given(assistantService.removeDuty(id, dutyToDelete)).willReturn(responseMessage);

        mockMvc.perform(delete("/api/assistant/duties/remove/{id}", id)
                        .param("duty", dutyToDelete)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(content().string(responseMessage));
    }

    @Test
    @DisplayName("Remove duty by Id throws User Not Found Exception")
    public void removeDutyByIdThrowsUserNotFoundException() throws Exception {
        String id = "3000";
        String dutyToRemove = "duty1";
        String response = "User with id: " + id + " does not exist or is not an Assistant";

        given(assistantService.removeDuty(id, dutyToRemove)).willThrow(new UserNotFoundException(response));

        mockMvc.perform(delete("/api/assistant/duties/remove/{id}", id)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("duty", dutyToRemove))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("404"))
                .andExpect(jsonPath("$.message").value(response))
                .andExpect(jsonPath("$.timestamp").exists());

    }

    ///  Courses tests
    @Test
    @DisplayName("Get courses by id successfully")
    public void getCoursesByIdSuccessfullyTests() throws Exception {
        String id = "10000";
        List<String> courseList = List.of("course1", "course2");
        assistantUser1.setCourseId(courseList);

        given(assistantService.getCoursesById(id)).willReturn(courseList);

        mockMvc.perform(get("/api/assistant/courses/{id}", id)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0]").value("course1"))
                .andExpect(jsonPath("$.[1]").value("course2"));
    }
    @Test
    @DisplayName("Get courses by id throws User Not Found Exception")
    public void getCoursesByIdThrowsUserNotFoundExceptionTest() throws Exception {
        String id = "30000";
        String response = "User with id: " + id + " does not exists or is not an Assistant";

        given(assistantService.getCoursesById(id)).willThrow(new UserNotFoundException(response));

        mockMvc.perform(get("/api/assistant/courses/{id}", id)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("404"))
                .andExpect(jsonPath("$.message").value(response))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @DisplayName("Add course by Id successfully")
    public void addCourseByIdSuccessfully() throws Exception {
        String id = "10000";
        String courseToAdd = "course 5";
        String response = "Course added successfully to id: " + id;

        given(assistantService.addCourseId(id, courseToAdd)).willReturn(response);

        mockMvc.perform(post("/api/assistant/courses/add/{id}", id)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("courseId", courseToAdd))
                .andExpect(status().isOk())
                .andExpect(content().string(response));
    }

    @Test
    @DisplayName("Add course by Id User Not Found Exception")
    public void addCourseByIdThrowsUserNotFoundExceptionTest() throws Exception {
        String id = "90000";
        String courseToAdd = "course 5";
        String response = "User with id: " + id + " does not exists or is not an Assistant";

        given(assistantService.addCourseId(id, courseToAdd)).willThrow(new UserNotFoundException(response));

        mockMvc.perform(post("/api/assistant/courses/add/{id}", id)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("courseId", courseToAdd))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("404"))
                .andExpect(jsonPath("$.message").value(response))
                .andExpect(jsonPath("$.timestamp").exists());;
    }
    @Test
    @DisplayName("Delete course by Id successfully")
    public void deleteCourseByIdSuccessfully() throws Exception {
        String id = "10000";
        String courseToDelete = "course1";
        String response = "Course removed successfully to user id: " + id;

        given(assistantService.removeCourseId(id, courseToDelete)).willReturn(response);

        mockMvc.perform(delete("/api/assistant/courses/delete/{id}", id)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("courseId", courseToDelete))
                .andExpect(status().isOk())
                .andExpect(content().string(response));
    }
    @Test
    @DisplayName("Delete course by Id throws User Not Found exception")
    public void deleteCourseByIdThrowsUserNotFoundException() throws Exception {
        String id = "90000";
        String courseToDelete = "course1";
        String response = "User with id: " + id + " does not exists or is not an Assistant";

        given(assistantService.removeCourseId(id, courseToDelete)).willThrow(new UserNotFoundException(response));

        mockMvc.perform(delete("/api/assistant/courses/delete/{id}", id)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("courseId", courseToDelete))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("404"))
                .andExpect(jsonPath("$.message").value(response))
                .andExpect(jsonPath("$.timestamp").exists());

    }
}



