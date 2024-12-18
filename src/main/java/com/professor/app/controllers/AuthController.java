package com.professor.app.controllers;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.professor.app.config.security.JwtUtils;
import com.professor.app.dto.users.AuthResponseDTO;
import com.professor.app.dto.users.LoginRequestDTO;
import com.professor.app.dto.users.UserResponseDTO;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
@NoArgsConstructor
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody LoginRequestDTO loginRequestDTO) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequestDTO.email(), loginRequestDTO.password()));
        if(authentication.isAuthenticated()) {
            String accessToken = jwtUtils.createAuthToken(authentication);
            String refreshToken = jwtUtils.createRefreshToken(authentication);

            return ResponseEntity.status(HttpStatus.OK).body(new AuthResponseDTO(accessToken, refreshToken));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new AuthResponseDTO(null, null));
    }
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestHeader("Authorization") String authorization){
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", "Missing or invalid authorization header"));
        }
        String refreshToken = authorization.substring(7);
        try {
            DecodedJWT decodedJWT = jwtUtils.validateToken(refreshToken);
            String username = jwtUtils.extractUsername(decodedJWT);
            Authentication authentication = new UsernamePasswordAuthenticationToken(username, null, AuthorityUtils.commaSeparatedStringToAuthorityList(decodedJWT.getClaim("authorities").asString()));
            System.out.println("Authentication: " + authentication) ;
            String newAccessToken = jwtUtils.createAuthToken(authentication);
            System.out.println("new access token: " + newAccessToken);
            return  ResponseEntity.ok(Collections.singletonMap("accessToken", newAccessToken));
        } catch (JWTVerificationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.singletonMap("error", "Invalid refresh token"));
        }
    }


}
