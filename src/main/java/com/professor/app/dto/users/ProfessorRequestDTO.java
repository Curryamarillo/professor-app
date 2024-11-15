package com.professor.app.dto.users;

import java.util.List;

public record ProfessorRequestDTO(String name,
                                  String surname,
                                  String email,
                                  String password,
                                  String dni,
                                  List<String> courseIds,
                                  List<String> courseNames,
                                  List<String> studentsIds
                                  ) {
}
