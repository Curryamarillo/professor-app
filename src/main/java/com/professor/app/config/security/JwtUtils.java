package com.professor.app.config.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.professor.app.entities.Token;
import com.professor.app.entities.User;
import com.professor.app.repositories.TokenRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
@NoArgsConstructor
public class JwtUtils {

    @Value("${security.jwt.key.private}")
    private String privateKey;

    @Value("${security.jwt.user.generator}")
    private String userGenerator;

    @Autowired
    private TokenRepository tokenRepository;

    private static final int AUTH_TOKEN_EXPIRATION_TIME = 60 * 60 * 1000;

    private static final int REFRESH_TOKEN_EXPIRATION_TIME = 7 * 24 * 60 * 60 * 1000;


    public String createAuthToken(Authentication authentication) {
        Algorithm algorithm = Algorithm.HMAC256(this.privateKey);
        User user = (User) authentication.getPrincipal();
        String authorities = authentication.getAuthorities()
                .stream().map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        String token = JWT.create()
                .withIssuer(this.userGenerator)
                .withSubject(user.getEmail())
                .withClaim("authorities", authorities)
                .withClaim("type", "authToken")
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis()+ AUTH_TOKEN_EXPIRATION_TIME))
                .withJWTId(UUID.randomUUID().toString())
                .withNotBefore(new Date(System.currentTimeMillis()))
                .sign(algorithm);
        Token tokenEntity = Token.builder()
                .token(token)
                .user(user)
                .isRefreshToken(false)
                .isRevoked(false)
                .issuedAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusSeconds(AUTH_TOKEN_EXPIRATION_TIME / 1000))
                .build();
        tokenRepository.save(tokenEntity);
        return token;
    }
    public String createRefreshToken(Authentication authentication) {
        Algorithm algorithm = Algorithm.HMAC256(this.privateKey);
        User user = (User) authentication.getPrincipal();

        String refreshToken = JWT.create()
                .withIssuer(this.userGenerator)
                .withSubject(user.getEmail())
                .withClaim("type", "refreshToken")
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION_TIME))
                .withJWTId(UUID.randomUUID().toString())
                .withNotBefore(new Date(System.currentTimeMillis()))
                .sign(algorithm);

        Token tokenEntity = Token.builder()
                .token(refreshToken)
                .user(user)
                .isRefreshToken(true)
                .isRevoked(false)
                .issuedAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusSeconds(REFRESH_TOKEN_EXPIRATION_TIME / 1000))
                .build();
        tokenRepository.save(tokenEntity);
        return refreshToken;
    }

    public DecodedJWT validateToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(this.privateKey);
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer(this.userGenerator)
                    .build();
            DecodedJWT decodedJWT = verifier.verify(token);
            boolean isRevoked = tokenRepository.findByToken(token)
                    .map(Token::isRevoked)
                    .orElse(true);
            if (isRevoked) {
                throw new JWTVerificationException("Token has been revoked");
            }

            return decodedJWT;

        } catch (JWTVerificationException exception) {
            System.out.println("Token verification failed: " + exception.getMessage());
            throw new JWTVerificationException("Token Invalid, not authorized: " + exception.getMessage());
        }
    }

    public boolean isRefreshToken(String token) {
        try {
            DecodedJWT decodedJWT = validateToken(token);
            return "refreshToken".equals(decodedJWT.getClaim("type").asString());
        } catch (JWTVerificationException exception) {
            System.out.println("Error validating token type: " + exception.getMessage());
            return false;
        }
    }
    public String extractUsername(DecodedJWT decodedJWT) {return decodedJWT.getSubject();}

    public Claim getSpecicficClaim(DecodedJWT decodedJWT, String claimName) {return  decodedJWT.getClaim(claimName);}
}
