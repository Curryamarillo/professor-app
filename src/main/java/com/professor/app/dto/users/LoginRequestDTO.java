package com.professor.app.dto.users;

import lombok.NonNull;

public record LoginRequestDTO(@NonNull String email,
                              @NonNull String password) {
}
