package com.fares.book_network.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.config.Customizer.withDefaults;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity(securedEnabled = true) // Enable secured annotation
public class SecurityConfig {


    private final JwtFilter jwtAuthFilter; //we need to declare it as final to be injected by spring because we used the @RequiredArgsConstructor annotation
    private final AuthenticationProvider authenticationProvider; //we need to declare it as final to be injected by spring because we used the @RequiredArgsConstructor annotation

    @Bean //Since the class is annotated with @Configuration we need to define beans for spring to pick up and put in the context
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        http
                .cors(withDefaults()) //spring will look for a bean of type CorsFilter and apply it to the filter chain
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(req ->
                        req.requestMatchers("/auth/**",
                                            "/v2/api-docs",
                                            "/v3/api-docs",
                                            "/v3/api-docs/**",
                                            "/swagger-resources",
                                            "/swagger-resources/**",
                                            "/configuration/ui",
                                            "/configuration/security",
                                            "/swagger-ui/**",
                                            "/webjars/**",
                                            "/swagger-ui.html",
                                        "/api/v1/books/**").permitAll()
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter
                        , UsernamePasswordAuthenticationFilter.class
                        ); //we pass by the jwtAuthFilter before the UsernamePasswordAuthenticationFilter
        return http.build();
    }
}
