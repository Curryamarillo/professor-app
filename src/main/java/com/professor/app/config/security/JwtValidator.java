package com.professor.app.config.security;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collection;

@Slf4j
@Component
@AllArgsConstructor
public class JwtValidator extends OncePerRequestFilter {

    @Autowired
    private final JwtUtils jwtUtils;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        log.debug("JwtValidator is being executed for request: {}", request.getRequestURI()); // Check if filter is invoked

        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String jwtToken = authHeader.substring(7);
            log.debug("JWT Token: {}", jwtToken); // Log the token itself (for debugging only - REMOVE IN PRODUCTION)

            try {
                DecodedJWT decodedJWT = jwtUtils.validateToken(jwtToken);
                log.debug("JWT Token Validated Successfully");

                String username = jwtUtils.extractUsername(decodedJWT);
                log.debug("Extracted Username: {}", username);

                String stringAuthorities = jwtUtils.getSpecicficClaim(decodedJWT, "authorities").asString();
                log.debug("Extracted Authorities (String): {}", stringAuthorities);

                if (!jwtUtils.isRefreshToken(jwtToken)) {
                    Collection<? extends  GrantedAuthority> authorities = AuthorityUtils.commaSeparatedStringToAuthorityList(stringAuthorities);
                    Authentication authenticationToken =  new UsernamePasswordAuthenticationToken(username, null, authorities);
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    log.debug("Authentication set in SecurityContextHolder");

                }
            } catch (JWTVerificationException e) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                log.error("JWT Verification Exception: {}", e.getMessage());

                return;
            }
        } else {
            log.debug("Authorization header is missing or does not start with Bearer");


        }
        filterChain.doFilter(request, response);
    }
}
