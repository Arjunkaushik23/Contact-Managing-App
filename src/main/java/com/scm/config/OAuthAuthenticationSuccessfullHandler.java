package com.scm.config;

import java.io.IOException;
import java.util.*;

import org.slf4j.Logger;

import com.scm.entities.Providers;
import com.scm.entities.User;
import com.scm.helpers.AppConstants;
import com.scm.repositories.UserRepo;

import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component

public class OAuthAuthenticationSuccessfullHandler implements
        AuthenticationSuccessHandler {

    Logger logger = LoggerFactory.getLogger(OAuthAuthenticationSuccessfullHandler.class);

    private final UserRepo userRepository;

    public OAuthAuthenticationSuccessfullHandler(UserRepo userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {

        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
        DefaultOAuth2User oauthUser = (DefaultOAuth2User) oauthToken.getPrincipal();

        String email = oauthUser.getAttribute("email");
        String name = oauthUser.getAttribute("name");
        String picture = oauthUser.getAttribute("picture");

        User user = userRepository.findByEmail(email)
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setUserId(UUID.randomUUID().toString()); // Manually setting the userId
                    newUser.setEmail(email);
                    newUser.setName(name);
                    newUser.setProfilePic(picture);
                    newUser.setProvider(
                            Providers.valueOf(oauthToken.getAuthorizedClientRegistrationId().toUpperCase()));
                    newUser.setRoleList(List.of(AppConstants.ROLE_USER));
                    return newUser;
                });

        userRepository.save(user);

        new DefaultRedirectStrategy().sendRedirect(request, response, "/user/dashboard");
    }

}
