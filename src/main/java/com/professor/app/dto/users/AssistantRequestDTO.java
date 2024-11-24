package com.professor.app.dto.users;

import java.util.List;
import java.util.Set;

public record AssistantRequestDTO(String name,
                                  String surname,
                                  String email,
                                  String password,
                                  String dni,
                                  List<String> courseId,
                                  Set<String> duties) {
}
