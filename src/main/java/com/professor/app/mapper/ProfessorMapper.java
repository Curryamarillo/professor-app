package com.professor.app.mapper;

import com.professor.app.dto.users.ProfessorRequestDTO;
import com.professor.app.entities.Professor;

public class ProfessorMapper {

    public static Professor toProfessor(ProfessorRequestDTO professorRequestDTO) {
        return  Professor.builder()
                .name(professorRequestDTO.name())
                .surname(professorRequestDTO.surname())
                .email(professorRequestDTO.email())
                .dni(professorRequestDTO.dni())
                .password(professorRequestDTO.password())
                .courseIds(professorRequestDTO.courseIds())
                .courseNames(professorRequestDTO.courseNames())
                .studentsIds(professorRequestDTO.studentsIds())
                .build();
    }
}
