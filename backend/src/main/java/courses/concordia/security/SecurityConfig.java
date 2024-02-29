package courses.concordia.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig{

    @Bean
    public SecurityFilterChain oauth2LoginFilterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(authz -> {
                    authz.requestMatchers(AntPathRequestMatcher.antMatcher("/")).permitAll(); // Allow unauthenticated access
                    authz.anyRequest().authenticated();
                })
                .csrf(csrf -> csrf.ignoringRequestMatchers(AntPathRequestMatcher.antMatcher("/**")))
                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin))
                .oauth2Login(oauth2 -> oauth2
                        //login unsuccessfully, redirect to /login
                        .defaultSuccessUrl("/auth/authorized")
                        .failureUrl("/login"))
                .logout(withDefaults())
                .build();
    }
}
