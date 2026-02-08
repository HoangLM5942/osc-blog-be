package app.onestepcloser.blog.config;

import app.onestepcloser.blog.security.CustomAuthenticationFilter;
import app.onestepcloser.blog.security.CustomAuthorizationFilter;
import app.onestepcloser.blog.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtUtil jwtUtil;
    private final JwtProperties jwtProperties;
    private final UserDetailsService userDetailsService;
    private final AuthenticationConfiguration authConfig;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        CustomAuthorizationFilter authorizationFilter =
                new CustomAuthorizationFilter(authenticationManager(authConfig), jwtUtil, userDetailsService);
        CustomAuthenticationFilter authenticationFilter =
                new CustomAuthenticationFilter(authenticationManager(authConfig), jwtUtil);
        authenticationFilter.setFilterProcessesUrl(jwtProperties.getLoginPath());
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(req -> "POST".equalsIgnoreCase(req.getMethod())
                                && req.getServletPath() != null
                                && req.getServletPath().equals(jwtProperties.getLoginPath())).permitAll()
                        .anyRequest().authenticated())
                .addFilter(authenticationFilter)
                .addFilterBefore(authorizationFilter, BasicAuthenticationFilter.class)
                .authenticationProvider(authenticationProvider());
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider(userDetailsService);
        authenticationProvider.setHideUserNotFoundExceptions(false);
        authenticationProvider.setPasswordEncoder(passwordEncoder);
        return authenticationProvider;
    }
}
