/*
package courses.concordia.rest;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig{

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(authz -> {
                            authz.requestMatchers("/").permitAll();// Allow unauthenticated access to these paths
                            authz.anyRequest().authenticated(); // Require authentication for any other request
                })
                .oauth2Login(withDefaults())
                .build();
    }
}
*/