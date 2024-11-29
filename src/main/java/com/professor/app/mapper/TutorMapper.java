package com.professor.app.mapper;

import com.professor.app.dto.users.TutorRequestDTO;
import com.professor.app.entities.Tutor;

public class TutorMapper {

    public static Tutor toTutor(TutorRequestDTO tutorRequestDTO) {
        return  Tutor.builder()
                .name(tutorRequestDTO.name())
                .surname(tutorRequestDTO.surname())
                .email(tutorRequestDTO.email())
                .password(tutorRequestDTO.password())
                .dni(tutorRequestDTO.dni())
                .tutoredStudentsId(tutorRequestDTO.tutoredStudentsId())
                .build();
    }
}
