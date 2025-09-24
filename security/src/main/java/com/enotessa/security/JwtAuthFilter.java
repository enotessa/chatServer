package com.enotessa.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        if (isAuthPath(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = jwtService.extractTokenFromHeaderFromRequest(request);
        if (token == null) {
            jwtService.sendUnauthorized(response, "Missing or invalid Authorization header");
            return;
        }

        String username = jwtService.extractUsername(token, response);
        if (username == null) {
            return;
        }

        if (jwtService.isAuthenticationRequired(username)) {
            jwtService.authenticateUser(request, token, username, response);
        }

        filterChain.doFilter(request, response);
    }

    private boolean isAuthPath(HttpServletRequest request) {
        return request.getServletPath().startsWith("/auth");
    }
}


