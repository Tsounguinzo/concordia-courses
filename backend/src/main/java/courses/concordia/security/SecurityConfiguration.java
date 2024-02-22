package courses.concordia.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
public class SecurityConfiguration{

    @Bean
    public SecurityFilterChain filterChain (HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorizeHttpRequests -> authorizeHttpRequests
                        .requestMatchers( "/api/courses", "/api/courses/**").permitAll()
                        .requestMatchers( HttpMethod.POST,"/api/courses/filter").permitAll()
                        .requestMatchers("/","auth/login").permitAll()
                        .anyRequest().authenticated()
                )
                //.csrf().disable() //for testing
                .logout(logout -> logout
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                        .logoutSuccessUrl("/").permitAll()
                );

        return http.build();
    }
}
