package com.professor.app.controllers;

import com.professor.app.dto.users.ProfessorRequestDTO;
import com.professor.app.dto.users.UserResponseDTO;
import com.professor.app.services.ProfessorService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/professor")
@AllArgsConstructor
public class ProfessorController {

    private ProfessorService professorService;

    @PostMapping("/create")
    public ResponseEntity<UserResponseDTO> createProfessor(@RequestBody ProfessorRequestDTO professorRequestDTO) {
        UserResponseDTO createdUser = professorService.saveProfessorUser(professorRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    // Course controllers

    @GetMapping("/courses/{id}")
    public ResponseEntity<Set<String>> getCoursesIdByProfessorId(@PathVariable String id) {
        Set<String> coursesId = professorService.getCourseIdListById(id);
        return ResponseEntity.status(HttpStatus.OK).body(coursesId);
    }
    @PostMapping("/courses/add/{id}")
    public ResponseEntity<String> addCoursesIdByProfessorId(@PathVariable String id, @RequestParam String courseId) {
        String courseAdded = professorService.addCourseIdById(id, courseId);
        return ResponseEntity.status(HttpStatus.OK).body(courseAdded);
    }
    @DeleteMapping("/courses/delete/{id}")
    public ResponseEntity<String> removeCourseIdByProfessorId(@PathVariable String id, @RequestParam String courseId) {
        String courseRemoved = professorService.removeCourseIdById(id, courseId);
        return ResponseEntity.status(HttpStatus.OK).body(courseRemoved);
    }

    // Students controllers

    @GetMapping("/students/{id}")
    public ResponseEntity<Set<String>> getStudentsByProfessorId(@PathVariable String id) {
        Set<String> studentsIds = professorService.getStudentIdList(id);
        return ResponseEntity.status(HttpStatus.OK).body(studentsIds);
    }

    @PostMapping("/students/{id}")
    public ResponseEntity<String> addOneStudentsIdByProfessorId(@PathVariable String id, @RequestParam String studentId) {
        String studentAdded = professorService.addStudentIdByProfessorId(id, studentId);
        return ResponseEntity.status(HttpStatus.OK).body(studentAdded);
    }
    @PostMapping("/students/list/{id}")
    public ResponseEntity<String> addStudentsIdsListByProfessorId(@PathVariable String id, @RequestParam Set<String> studentList) {
        String studentsAdded = professorService.addMultipleStudentsId(id,studentList);
        return ResponseEntity.status(HttpStatus.OK).body(studentsAdded);
    }
    @DeleteMapping("/students/delete/{id}")
    public ResponseEntity<String> deleteStudentIdByProfessorId(@PathVariable String id, @RequestParam String studentId) {
        String studentDeleted = professorService.deleteStudentIdByProfessorId(id, studentId);
        return ResponseEntity.status(HttpStatus.OK).body(studentDeleted);
    }
    @DeleteMapping("/students/delete-list/{id}")
    public ResponseEntity<String> deleteStudentsIdListByProfessorId(@PathVariable String id, @RequestParam Set<String> studentList) {
        String studentListToDelete = professorService.deleteStudentsIdListByProfessorId(id, studentList);
        return ResponseEntity.status(HttpStatus.OK).body(studentListToDelete);
    }
}
