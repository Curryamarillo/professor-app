package com.professor.app.controllers;

import com.professor.app.dto.users.StudentRequestDTO;
import com.professor.app.dto.users.UserResponseDTO;
import com.professor.app.services.StudentService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/student")
@AllArgsConstructor
public class StudentControllers {

    private StudentService studentService;

    @PostMapping("/create")
    public ResponseEntity<UserResponseDTO> createStudentUser(@RequestBody StudentRequestDTO studentRequestDTO) {
        UserResponseDTO createdUser = studentService.saveStudentUser(studentRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    @GetMapping("/courses/{id}")
    public ResponseEntity<Set<String>> getEnrolledCourseIDByStudentID(@PathVariable String id) {
        Set<String> enrolledCoursesID = studentService.getEnrolledCourseIDByStudentID(id);
        return ResponseEntity.ok(enrolledCoursesID);
    }
    @PostMapping("course/add/{id}")
    public ResponseEntity<String> addEnrolledCourseIDbyStudentID(@PathVariable String id, @RequestParam String courseID) {
        String response = studentService.addEnrolledCourseIDByStudentID(id, courseID);
        return ResponseEntity.ok(response);
    }
    @DeleteMapping("/courses/{id}")
    public ResponseEntity<String> deleteCourseIDByStudentID(@PathVariable String id, @RequestParam String courseId) {
        String response = studentService.removeEnrolledCourseIDByStudentID(id, courseId);
        return ResponseEntity.ok(response);
    }
}
