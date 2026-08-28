package com.mariver.auth.security;

import com.mariver.user.UserRepository;
import com.mariver.auth.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    /*
     * This filter runs once per request.
     *
     * Flow:
     * 1. Read Authorization header.
     * 2. Check if it starts with "Bearer ".
     * 3. Extract JWT.
     * 4. Extract email from JWT.
     * 5. Load user from database.
     * 6. Validate token.
     * 7. Put authenticated user into Spring SecurityContext.
     * 8. Continue to controller.
     */
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain ) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        /*
         * No token, or token is not Bearer token.
         * Continue without authenticating.
         *
         * If the endpoint is protected, Spring Security will reject it later.
         */
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        String email = jwtService.extractEmail(token);
        System.out.println(token);
        System.out.println(email);

        /*
         * Only authenticate if:
         * - email exists in token
         * - no authentication is already set
         */
        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            var user = userRepository.findByEmail(email)
                    .orElse(null);

            if (user != null && jwtService.isTokenValid(token, user.getEmail())) {

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                user,
                                null,
                                List.of()
                        );

                authentication.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        filterChain.doFilter(request, response);
    }


    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return request.getServletPath().startsWith("/api/auth/")
                || request.getMethod().equalsIgnoreCase("OPTIONS");
    }
}
