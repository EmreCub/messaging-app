package com.example.mobileapp;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String authorizationHeader = request.getHeader("Authorization");

        System.out.println("DEBUG: Incoming Request URI: " + request.getRequestURI());

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7); // Extract token after "Bearer "
            String email = null;

            try {
                email = jwtUtil.extractEmail(token); // Extract email from token
                System.out.println("DEBUG: Extracted Email: " + email);

                if (email != null && jwtUtil.validateToken(token, email)) {
                    System.out.println("DEBUG: Token is valid for user: " + email);

                    // Set authentication context for Spring Security
                    UserDetails userDetails = User.builder()
                            .username(email)
                            .password("") // Password is not required for JWT authentication
                            .authorities("ROLE_USER") // Adjust roles/authorities as per your app's needs
                            .build();

                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    System.out.println("DEBUG: Authentication context set for user: " + email);
                } else {
                    System.out.println("DEBUG: Invalid JWT token");
                }
            } catch (Exception e) {
                System.out.println("DEBUG: Exception during JWT validation - " + e.getMessage());
            }
        } else {
            System.out.println("DEBUG: No Authorization header found or does not start with 'Bearer '");
        }

        chain.doFilter(request, response); // Continue the filter chain
    }
}
