package com.qosocial.qosocialapi.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(c -> c.disable())
                .headers(h -> h
                        .contentSecurityPolicy(p -> p
                                .policyDirectives("frame-ancestors 'self'")
                        )
                )
                .authorizeHttpRequests(r -> r
                        .anyRequest().permitAll());

        return http.build();
    }

}
