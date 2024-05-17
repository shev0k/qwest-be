package com.qwest.backend.configuration.security;

import com.qwest.backend.configuration.security.token.JwtRequestFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.http.HttpMethod;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final String STAY_LISTINGS_PATH = "/api/stay-listings/**";
    private static final String AUTHORS_PATH = "/api/authors/**";
    private static final String ROLE_FOUNDER = "FOUNDER";
    private static final String ROLE_HOST = "HOST";
    private static final String ROLE_TRAVELER = "TRAVELER";

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtRequestFilter jwtRequestFilter) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/api/authors/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/authors").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/amenities").permitAll()
                        .requestMatchers(HttpMethod.GET, STAY_LISTINGS_PATH).permitAll()
                        .requestMatchers(HttpMethod.GET, AUTHORS_PATH).permitAll()
                        .requestMatchers(HttpMethod.POST, STAY_LISTINGS_PATH).hasAnyRole(ROLE_FOUNDER, ROLE_HOST)
                        .requestMatchers(HttpMethod.PUT, STAY_LISTINGS_PATH).hasAnyRole(ROLE_FOUNDER, ROLE_HOST)
                        .requestMatchers(HttpMethod.DELETE, STAY_LISTINGS_PATH).hasAnyRole(ROLE_FOUNDER, ROLE_HOST)
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html", "/swagger-resources/**").permitAll()
                        .anyRequest().authenticated())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
