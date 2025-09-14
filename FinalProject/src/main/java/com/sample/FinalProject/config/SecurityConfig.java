package com.sample.FinalProject.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.sample.FinalProject.service.CustomUserDetailsService;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public UserDetailsService userDetailsService(CustomUserDetailsService customUserDetailsService) {
        return customUserDetailsService;
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider(UserDetailsService userDetailsService) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(NoOpPasswordEncoder.getInstance());
        return provider;
    }
    
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers("/admin/**").hasAuthority("Admin")
                    .requestMatchers("/vendor/dashboard").hasAuthority("Vendor")
                    .requestMatchers("/vendor/dashboard/**")
                    .access((authenticationSupplier, context) -> {
                        var authentication = authenticationSupplier.get();
                        if (authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() instanceof CustomUserDetails user && user.isApproved()) 
                        {
                            return new AuthorizationDecision(true);
                        } 
                        else 
                        {
                            return new AuthorizationDecision(false);
                        }
                    })
                    .requestMatchers("/vendor/**").hasAuthority("Vendor")
                    .requestMatchers("/trainer/dashboard").hasAnyAuthority("Trainer", "Admin")
                    .requestMatchers("/trainer/dashboard/**")
                    .access((authenticationSupplier, context) -> {
                        var authentication = authenticationSupplier.get();
                        if (authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() instanceof CustomUserDetails user && user.isApproved()) 
                        {
                            return new AuthorizationDecision(true);
                        } 
                        else 
                        {
                            return new AuthorizationDecision(false);
                        }
                    })
                    .requestMatchers("/trainer/**").hasAnyAuthority("Trainer", "Admin")
                    .anyRequest().permitAll()
                )
            .formLogin(form -> form
                    .loginPage("/login")   
                    .usernameParameter("email")
                    .passwordParameter("password")
                    .defaultSuccessUrl("/default")  
                    .permitAll()
                )
            .logout(logout -> logout.disable());
        return http.build();
    }
}
