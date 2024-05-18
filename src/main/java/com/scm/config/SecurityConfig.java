package com.scm.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.web.filter.HiddenHttpMethodFilter;

import com.scm.services.impl.SecurityCustomUserDetailService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // DAO authentication provider

    private final SecurityCustomUserDetailService service;
    private final OAuthAuthenticationSuccessfullHandler oAuthAuthenticationSuccessfullHandler;

    // configuration of authentication provider :

    public SecurityConfig(SecurityCustomUserDetailService service,
            OAuthAuthenticationSuccessfullHandler oAuthAuthenticationSuccessfullHandler) {
        this.service = service;
        this.oAuthAuthenticationSuccessfullHandler = oAuthAuthenticationSuccessfullHandler;
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {

        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();

        // user detail sevice object
        daoAuthenticationProvider.setUserDetailsService(service);
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());

        return daoAuthenticationProvider;
    }

    // configure spring security filter chain

    @Bean
    public SecurityFilterChain chain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth ->
        // auth.requestMatchers("/home", "/signup", "/about", "services").permitAll()
        auth.requestMatchers("/user/**").authenticated()
                .anyRequest().permitAll());

        http.formLogin(form -> {
            form.loginPage("/login");
            form.loginProcessingUrl("/authenticate");
            form.successForwardUrl("/user/dashboard");
            form.failureForwardUrl("/login?error=true");
            form.usernameParameter("email");
            form.passwordParameter("password");

        });

        http.csrf(AbstractHttpConfigurer::disable);
        http.logout(logout -> {
            logout.logoutUrl("/do-logout");
            logout.logoutSuccessUrl("/login?logout=true");
        });

        // oauth configuration
        http.oauth2Login(oauth -> {
            oauth.loginPage("/login");
            oauth.successHandler(myAuthenticationSuccessHandler());
        });

        return http.build();

    }

    @Bean
    public AuthenticationSuccessHandler myAuthenticationSuccessHandler() {
        return new SimpleUrlAuthenticationSuccessHandler() {
            @Override
            protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response,
                    Authentication authentication) {
                // You can include logic here to determine the target URL based on the role or
                // other factors
                return "/user/dashboard";
            }
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public HiddenHttpMethodFilter hiddenHttpMethodFilter() {
        return new HiddenHttpMethodFilter();
    }
}
