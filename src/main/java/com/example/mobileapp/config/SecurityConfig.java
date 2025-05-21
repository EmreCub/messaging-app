package com.example.mobileapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.example.mobileapp.JwtFilter;

@Configuration
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authConfig
    ) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
          // turn off CSRF because we’re a pure REST API
          .csrf(csrf -> csrf.disable())

          // make sure we don’t create a session—JWT is stateless
          .sessionManagement(sm -> 
            sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
          )

          // configure which endpoints are public vs protected
          .authorizeHttpRequests(auth -> auth
            // allow anyone to call these two endpoints
            .requestMatchers(
               HttpMethod.POST,
               "/api/users/register",
               "/api/users/login"
             )
             .permitAll()

            // everything else needs a valid JWT
            .anyRequest().authenticated()
          )

          // plug in your JWT filter
          .addFilterBefore(
            jwtFilter,
            UsernamePasswordAuthenticationFilter.class
          );

        return http.build();
    }
}