package com.professor.app.controllers;

import com.professor.app.dto.users.TutorRequestDTO;
import com.professor.app.dto.users.UserResponseDTO;
import com.professor.app.services.TutorService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("api/tutor")
@AllArgsConstructor
public class TutorController {

    @Autowired
    private TutorService tutorService;

    @PostMapping("/create")
    public ResponseEntity<UserResponseDTO> createTutorUser(@RequestBody TutorRequestDTO tutorRequestDTO) {
        UserResponseDTO createdUser = tutorService.saveTutorUser(tutorRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    @GetMapping("/student-list/{id}")
    public ResponseEntity<Set<String>> getStudentIdList(@PathVariable String id) {
        Set<String> studentList = tutorService.getStudentsIDsByTutorId(id);
        return ResponseEntity.status(HttpStatus.OK).body(studentList);
    }

    @PostMapping("/student/{id}")
    public ResponseEntity<String> addStudentIdByTutorId(@PathVariable String id, @RequestParam String studentId) {
        String addStudent = tutorService.addStudentIDByTutorID(id, studentId);
        return ResponseEntity.status(HttpStatus.OK).body(addStudent);
    }

    @PostMapping("/student-list/{id}")
    public ResponseEntity<String> addStudentListByTutorId(@PathVariable String id, @RequestParam List<String> studentIdList) {
        String addList = tutorService.addMultipleStudentsIDByTutorID(id, studentIdList);
        return ResponseEntity.status(HttpStatus.OK).body(addList);
    }

    @DeleteMapping("/student/{id}")
    public ResponseEntity<String> deleteStudentIdByTutorId(@PathVariable String id, @RequestParam String studentId) {
        String deleteStudent = tutorService.deleteStudentIDByTutorId(id, studentId);
        return ResponseEntity.status(HttpStatus.OK).body(deleteStudent);
    }

    @DeleteMapping("/student-list/{id}")
    public ResponseEntity<String> deleteStudentIdListByTutorId(@PathVariable String id, @RequestParam Set<String> studentIdListToDelete) {
        String deleteStudentList = tutorService.deleteStudentsIDListByTutorID(id, studentIdListToDelete);
        return ResponseEntity.status(HttpStatus.OK).body(deleteStudentList);
    }
}
