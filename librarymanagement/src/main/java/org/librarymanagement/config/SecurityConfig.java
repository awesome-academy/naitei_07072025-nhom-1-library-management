package org.librarymanagement.config;

import org.librarymanagement.constant.ApiEndpoints;
import org.librarymanagement.repository.UserRepository;
import org.librarymanagement.security.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public UserDetailsService userDetailsService(UserRepository userRepository) {
        return new CustomUserDetailsService(userRepository);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Order(0)
    public SecurityFilterChain resources(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/public/**")
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .requestCache(AbstractHttpConfigurer::disable)
                .securityContext(AbstractHttpConfigurer::disable)
                .sessionManagement(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable);
        return http.build();
    }

    @Bean
    @Order(1) // Ưu tiên cao hơn cho API
    public SecurityFilterChain apiSecurity(HttpSecurity http) throws Exception {
        http
                .securityMatcher(ApiEndpoints.BASE_USER_URI + "/**") // Áp dụng cho URL bắt đầu bằng /api/
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(ApiEndpoints.USER_AUTH + "/**").permitAll()
                        .requestMatchers(ApiEndpoints.BASE_USER_URI + "/**").hasAnyRole("USER", "ADMIN")
                        .anyRequest().authenticated()
                );
        return http.build();
    }

    @Bean
    @Order(2) // Cho admin MVC
    public SecurityFilterChain adminSecurity(HttpSecurity http) throws Exception {
        http
                .securityMatcher(ApiEndpoints.BASE_ADMIN_URI + "/**") // Áp dụng cho URL bắt đầu bằng /admin/
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(ApiEndpoints.ADMIN_AUTH + "/**", "/css/**", "/js/**").permitAll()
                        .requestMatchers(ApiEndpoints.BASE_ADMIN_URI + "/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .formLogin(AbstractHttpConfigurer::disable)
                .logout(LogoutConfigurer::permitAll)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                        .invalidSessionUrl(ApiEndpoints.ADMIN_AUTH + "/login?expired")
                        .maximumSessions(1)
                        .expiredUrl(ApiEndpoints.ADMIN_AUTH + "/login?expired")
                );
        return http.build();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/css/**", "/js/**")
                .requestCache(AbstractHttpConfigurer::disable)
                .securityContext(AbstractHttpConfigurer::disable)
                .sessionManagement(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)

                .authorizeHttpRequests(authorizationManagerRequestMatcherRegistry -> {
                    authorizationManagerRequestMatcherRegistry.requestMatchers("/req/**","/css/**","/js/**").permitAll();
                    authorizationManagerRequestMatcherRegistry.anyRequest().authenticated();
                })

                .formLogin(httpSecurityFormLoginConfigurer -> {
                    httpSecurityFormLoginConfigurer.loginPage("/req/login").permitAll();
                    httpSecurityFormLoginConfigurer.defaultSuccessUrl("/index");
                });
        return http.build();
    }
}
