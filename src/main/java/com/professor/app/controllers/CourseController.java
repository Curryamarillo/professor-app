package com.professor.app.controllers;

import com.professor.app.dto.courses.CourseRequestDTO;
import com.professor.app.entities.Course;
import com.professor.app.services.CourseService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/course")
@AllArgsConstructor
public class CourseController {

    private CourseService courseService;

    @PostMapping("/create")
    public ResponseEntity<String> createCourse(@RequestBody CourseRequestDTO courseRequestDTO) {
        String response = courseService.createCourse(courseRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    @GetMapping()
    public ResponseEntity<List<Course>> listAllCourses() {
        List<Course> courseList = courseService.findAllCourses();
        return ResponseEntity.status(HttpStatus.OK).body(courseList);
    }
    @GetMapping("/get/{id}")
    public ResponseEntity<Course> getCourseByID(@PathVariable String id) {
        Course course = courseService.getCourseById(id);
        return ResponseEntity.status(HttpStatus.OK).body(course);
    }
    @GetMapping("/get-code/{code}")
    public ResponseEntity<Course> getCourseByCode(@PathVariable String code) {
        Course course = courseService.getCourseByCode(code);
        return ResponseEntity.status(HttpStatus.OK).body(course);
    }
    @GetMapping("/student-at/{id}")
    public ResponseEntity<Set<String>> getStudentList(@PathVariable String id) {
        Set<String> studentListID = courseService.getStudentListId(id);
        return ResponseEntity.status(HttpStatus.OK).body(studentListID);
    }
    @PutMapping("/update/{id}")
    public ResponseEntity<String> updateCourse(@PathVariable String id, @RequestParam String code, @RequestParam String name, @RequestParam String comments) {
        String response = courseService.updateCourseByID(id,code, name, comments);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
    @PatchMapping("/update-list/{id}")
    public ResponseEntity<String> updateStudentAtCourseIDList(@PathVariable String id,@RequestParam String oldStudentID,@RequestParam String newStudentID) {
        String response = courseService.updateStudentIDToCourse(id, oldStudentID, newStudentID);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/add-student/{id}")
    public ResponseEntity<String> addStudentIdToACourse(@PathVariable String id, @RequestParam String studentID) {
        String response = courseService.addStudentIDToCourse(id, studentID);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
    @DeleteMapping("/delete-student/{id}")
    public ResponseEntity<String> deleteStudentByIDAtACourseID(@PathVariable String id, @RequestParam String studentId) {
        String response = courseService.deleteStudentIDToCourseByID(id, studentId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
    @DeleteMapping("/delete-all-students/{id}")
    public ResponseEntity<String> deleteAllStudentsOfACourse(@PathVariable String id) {
        String response = courseService.deleteAllStudentsIdFromACourse(id);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
