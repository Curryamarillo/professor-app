package com.professor.app.mapper;

import com.professor.app.dto.users.ProfessorRequestDTO;
import com.professor.app.dto.users.StudentRequestDTO;
import com.professor.app.entities.Student;

public class StudentMapper {

    public static Student toStudent(StudentRequestDTO studentRequestDTO) {
        return Student.builder()
                .name(studentRequestDTO.name())
                .surname(studentRequestDTO.surname())
                .dni(studentRequestDTO.dni())
                .email(studentRequestDTO.email())
                .password(studentRequestDTO.password())
                .enrolledCoursesId(studentRequestDTO.enrolledCoursesId())
                .build();
    }
}
