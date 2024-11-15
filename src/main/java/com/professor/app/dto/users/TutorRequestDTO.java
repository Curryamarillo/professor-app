package com.professor.app.dto.users;

import java.util.List;

public record TutorRequestDTO(String name,
                              String surname,
                              String email,
                              String password,
                              String dni,
                              List<String> tutoredStudentsId) {
}
