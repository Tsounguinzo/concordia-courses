package courses.concordia.security;


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
    private final RateLimitFilter rateLimitFilter;

    private final AuthenticationProvider authenticationProvider;
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        return http
                .requiresChannel(channel -> channel.anyRequest().requiresSecure())//enforce https
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/api/v1/instructors/**").permitAll() // Allow unauthenticated access
                        .requestMatchers("/api/v1/grades/**").permitAll() // Allow unauthenticated access
                        .requestMatchers("/api/v1/courses/**").permitAll() // Allow unauthenticated access
                        .requestMatchers("/api/v1/reviews/**").permitAll() // Allow unauthenticated access
                        .requestMatchers("/api/v1/interactions/**").permitAll() // Allow unauthenticated access
                        .requestMatchers("/api/v1/auth/user", "/api/v1/auth/reset_password", "/api/v1/auth/forgot_password").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/signin", "/api/v1/auth/signup").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/api/v1/auth/update_password").permitAll()
                        .anyRequest().authenticated()
                )
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(rateLimitFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwAuthenFilter, UsernamePasswordAuthenticationFilter.class)
                .csrf().disable()
                .build();
    }
}
