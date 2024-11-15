package com.professor.app.dto.users;

import java.util.List;

public record StudentRequestDTO(String name,
                                String surname,
                                String email,
                                String password,
                                String dni,
                                List<String> enrolledCoursesId) {
}
