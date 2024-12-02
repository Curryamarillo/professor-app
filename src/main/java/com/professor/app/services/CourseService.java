package com.professor.app.services;

import com.professor.app.dto.courses.CourseRequestDTO;
import com.professor.app.entities.Course;
import com.professor.app.repositories.CourseRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@AllArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;

    public String createCourse(CourseRequestDTO courseRequestDTO) {
        if (courseRepository.existsByCode(courseRequestDTO.code())) {
            throw new IllegalArgumentException("A course with code " + courseRequestDTO.code() + " already exists");
        }

        Course courseToAdd = Course.builder()
                .code(courseRequestDTO.code())
                .name(courseRequestDTO.name())
                .comments(courseRequestDTO.comments())
                .studentsId(courseRequestDTO.studentListId() != null ? courseRequestDTO.studentListId() : new HashSet<>())
                .build();

        Course savedCourse = courseRepository.save(courseToAdd);

        return "Course added successfully with ID: " + savedCourse.getId();
    }
    public List<Course> findAllCourses() {
        return courseRepository.findAll();
    }

    public Course getCourseById(String id) {
        return findCourseById(id);
    }
    public Course getCourseByCode(String code) {
        return courseRepository.findByCode(code)
                .orElseThrow(() -> new IllegalArgumentException("Course with Code: " + code + " not found"));
    }
    public Set<String> getStudentListId(String id){
        Course course = findCourseById(id);
        return course.getStudentsId();
    }

    public String updateCourseByID(String id,String code, String name, String comments) {
        Course course = findCourseById(id);
        course.setCode(code);
        course.setName(name);
        course.setComments(comments);
        courseRepository.save(course);
        return "Course with ID: " + id + " updated successfully";
    }
    public String addStudentIDToCourse(String id, String studentId) {
        Course course = findCourseById(id);
        course.getStudentsId().add(studentId);
        return "Student added successfully to course ID: " + id;
    }
    public String updateStudentIDToCourse(String id, String oldStudentID, String newStudentID) {
        Course course = findCourseById(id);
        course.getStudentsId().remove(oldStudentID);
        course.getStudentsId().add(newStudentID);
        courseRepository.save(course);
        return "Student updated successfully al course with ID: " + id;
    }
    public String deleteStudentIDToCourseByID(String id, String studentId) {
        Course course = findCourseById(id);
        if (!course.getStudentsId().contains(studentId)) {
            throw new IllegalArgumentException("Course does not contain Student ID: " + id);
        }
        course.getStudentsId().remove(studentId);
        courseRepository.save(course);
        return "Student removed successfully";
    }
    public String deleteAllStudentsIdFromACourse(String id) {
        Course course = findCourseById(id);
        course.getStudentsId().clear();
        courseRepository.save(course);
        return "All students removed successfully";
    }
    /// find course by id
    private Course findCourseById(String id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Course with ID: " + id + " not found"));
    }
}
