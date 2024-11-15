package com.professor.app.dto.users;

import java.util.List;

public record AssistantRequestDTO(String name,
                                  String surname,
                                  String email,
                                  String password,
                                  String dni,
                                  List<String> courseId,
                                  List<String> duties) {
}
