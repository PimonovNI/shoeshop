package com.example.shoeshop.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UserDetailsService userDetailsService;

    @Autowired
    public SecurityConfig(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests()
                .requestMatchers("/css/*", "/img/*", "/", "/filter", "/shoes_details/*", "/auth/*", "/auth/activation/*", "/error")
                            .permitAll()
                .requestMatchers("/admin", "/admin/*", "/admin/**").hasRole("ADMIN")
                .anyRequest().hasAnyRole("ADMIN", "USER")
                .and()
                .httpBasic()
                .and()
                .formLogin()
                .loginPage("/auth/login")
                .loginProcessingUrl("/process_login")
                .defaultSuccessUrl("/")
                .failureUrl("/auth/login?error")
                .and()
                .logout()
                .logoutUrl("/auth/logout")
                .logoutSuccessUrl("/auth/login")
                .and()
                .userDetailsService(userDetailsService);

        return http.build();
    }

    @Bean
    public PasswordEncoder getPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }

}
