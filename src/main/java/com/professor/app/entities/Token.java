package com.professor.app.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "tokens")
@Builder
public class Token {

    @Id
    private String id;

    private String token;


    private String username;

    private boolean isRevoked;

    private boolean isRefreshToken;

    private LocalDateTime issuedAt;

    private LocalDateTime expiresAt;
}
