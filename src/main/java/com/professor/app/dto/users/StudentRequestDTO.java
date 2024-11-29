package com.professor.app.dto.users;

import java.util.List;
import java.util.Set;

public record StudentRequestDTO(String name,
                                String surname,
                                String email,
                                String password,
                                String dni,
                                Set<String> enrolledCoursesId) {
}
