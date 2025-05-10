package ru.calendorny.taskservice.controller;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // отключаем CSRF
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll() // разрешаем все запросы
            )
            .httpBasic(basic -> basic.disable()) // отключаем Basic Auth
            .formLogin(login -> login.disable()); // отключаем форму логина

        return http.build();
    }
}

