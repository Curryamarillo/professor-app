package com.professor.app.controllers;

import com.professor.app.dto.users.AssistantRequestDTO;
import com.professor.app.dto.users.UserResponseDTO;
import com.professor.app.services.AssistantService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/assistant")
@AllArgsConstructor
public class AssistantController {

    @Autowired
    private AssistantService assistantService;

    @PostMapping("/create")
    public ResponseEntity<UserResponseDTO> createAssistant(@RequestBody AssistantRequestDTO assistantRequestDTO) {
        UserResponseDTO createdUser = assistantService.saveAssistant(assistantRequestDTO);
        return  ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    /// Course controllers

    @GetMapping("/courses/{id}")
    public ResponseEntity<List<String>> getCoursesById(@PathVariable String id) {
        List<String> courseList = assistantService.getCoursesById(id);
        return ResponseEntity.status(HttpStatus.OK).body(courseList);
    }

    @PatchMapping("/courses/update/{id}")
    public ResponseEntity<String> updateCourses(@PathVariable String id,
                                                @RequestParam String courseId) {
        String updatedResponse = assistantService.updateCourseId(id, courseId);
        return ResponseEntity.status(HttpStatus.OK).body(updatedResponse);
    }

    @PostMapping("/courses/add/{id}")
    public ResponseEntity<String> addCourses(@PathVariable String id,
                                             @RequestParam String courseId) {
        String courseAdded = assistantService.addCourseId(id, courseId);
        return ResponseEntity.status(HttpStatus.OK).body(courseAdded);
    }
    @DeleteMapping("/courses/delete/{id}")
    public ResponseEntity<String> removeCourses(@PathVariable String id,
                                                @RequestParam String courseId) {
        String courseRemoved = assistantService.removeCourseId(id, courseId);
        return ResponseEntity.status(HttpStatus.OK).body(courseRemoved);
    }


/// Duties controllers

    @GetMapping("/duties/{id}")
    public ResponseEntity<Set<String>> getDutiesById(@PathVariable String id) {
    Set<String> dutiesList = assistantService.getDutiesById(id);
    return ResponseEntity.status(HttpStatus.OK).body(dutiesList);
}

    @PostMapping("/duties/add/{id}")
    public ResponseEntity<String> addDuty(@PathVariable String id, @RequestParam String duty) {
    String addedDutyResult = assistantService.addDutyById(id, duty);
    return ResponseEntity.status(HttpStatus.OK).body(addedDutyResult);
}
    @DeleteMapping("/duties/remove/{id}")
    public ResponseEntity<String> removeDuty(@PathVariable String id, @RequestParam String duty) {
        String removeDutyResponse = assistantService.removeDuty(id, duty);
        return ResponseEntity.status(HttpStatus.OK).body(removeDutyResponse);
    }

}
