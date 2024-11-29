package com.professor.app.mapper;

import com.professor.app.dto.users.AdminRequestDTO;
import com.professor.app.entities.Admin;

public class AdminMapper {

    public static Admin toAdmin(AdminRequestDTO adminRequestDTO) {
        return Admin.builder()
                .name(adminRequestDTO.name())
                .surname(adminRequestDTO.surname())
                .email(adminRequestDTO.email())
                .password(adminRequestDTO.password())
                .dni(adminRequestDTO.dni())
                .comments(adminRequestDTO.comments())
                .build();

    }
}
