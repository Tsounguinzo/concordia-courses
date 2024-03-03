package courses.concordia.security;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig{

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/api/v1/courses/**").permitAll() // Allow unauthenticated access
                        .requestMatchers("/api/v1/auth/**").permitAll() // Allow unauthenticated access
                        .requestMatchers("/api/v1/reviews/**").permitAll() // Allow unauthenticated access
                        .requestMatchers("/api/v1/interactions/**").permitAll() // Allow unauthenticated access
                        .requestMatchers("/api/v1/notifications/**").permitAll() // Allow unauthenticated access
                        .requestMatchers("/api/v1/subscriptions/**").permitAll() // Allow unauthenticated access
                        .requestMatchers(HttpMethod.DELETE,"/api/v1/reviews/**").permitAll() // Allow unauthenticated access
                        .requestMatchers(HttpMethod.PUT,"/api/v1/reviews/**").permitAll() // Allow unauthenticated access
                        //.anyRequest().authenticated()
                )
                .csrf().disable()
                //.oauth2Login(withDefaults())
                .build();
    }
}
