package com.professor.app.dto.users;

import java.util.Set;

public record TutorRequestDTO(String name,
                              String surname,
                              String email,
                              String password,
                              String dni,
                              Set<String> tutoredStudentsId) {
}
