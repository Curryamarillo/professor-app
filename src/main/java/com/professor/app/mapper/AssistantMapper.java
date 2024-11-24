package com.professor.app.mapper;

import com.professor.app.dto.users.AssistantRequestDTO;
import com.professor.app.entities.Assistant;

import java.util.Set;

public class AssistantMapper {

    public static Assistant toAssistant(AssistantRequestDTO assistantRequestDTO) {
        return Assistant.builder()
                .name(assistantRequestDTO.name())
                .surname(assistantRequestDTO.surname())
                .dni(assistantRequestDTO.dni())
                .email(assistantRequestDTO.email())
                .password(assistantRequestDTO.password())
                .courseId(assistantRequestDTO.courseId())
                .duties(assistantRequestDTO.duties())
                .build();

    }
}
