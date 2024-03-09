package courses.concordia.security;


import jakarta.servlet.Filter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig{

    private final JwtAuthenticationFilter jwAuthenFilter;

    private final AuthenticationProvider authenticationProvider;
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        return http
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/api/v1/courses/**","/api/v1/auth/signup","/api/v1/auth/signin","/api/v1/auth/**").permitAll() // Allow unauthenticated access
                        .requestMatchers("/api/v1/reviews/**").permitAll() // Allow unauthenticated access
                        .requestMatchers("/api/v1/interactions/**").permitAll() // Allow unauthenticated access
                        .requestMatchers("/api/v1/notifications/**").permitAll() // Allow unauthenticated access
                        .requestMatchers("/api/v1/subscriptions/**").permitAll() // Allow unauthenticated access
                        .requestMatchers(HttpMethod.DELETE,"/api/v1/reviews/**").permitAll() // Allow unauthenticated access
                        .requestMatchers(HttpMethod.PUT,"/api/v1/reviews/**").permitAll() // Allow unauthenticated access
                        .anyRequest().authenticated()
                )
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwAuthenFilter, UsernamePasswordAuthenticationFilter.class)
                .csrf().disable()
                //.oauth2Login(withDefaults())
                .build();
    }
}
