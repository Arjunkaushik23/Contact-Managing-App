package com.scm.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.scm.entities.Contact;
import com.scm.entities.User;
import com.scm.services.ContactService;
import com.scm.services.UserService;

@Controller
@RequestMapping("/user")
public class UserController {

    private final ContactService contactService;
    private final UserService userService;
    Logger logger = LoggerFactory.getLogger(UserController.class);

    // Constructor
    public UserController(ContactService contactService, UserService userService) {
        this.contactService = contactService;
        this.userService = userService;
    }

    /**
     * Retrieves the username from the Authentication object.
     *
     * @param authentication the Authentication object
     * @return the username or null if not found
     */
    private String getUsernameFromAuthentication(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        String username = null;

        if (principal instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) principal;
            username = userDetails.getUsername();
        } else if (principal instanceof DefaultOAuth2User) {
            DefaultOAuth2User oauthUser = (DefaultOAuth2User) principal;
            username = oauthUser.getAttribute("email");

            if (username == null) {
                username = oauthUser.getAttribute("login") + "@gmail.com"; // Use login attribute as fallback
                logger.info("User attribute for login is: " + username);
            }
        }

        return username;
    }

    // User dashboard
    @RequestMapping(value = "/dashboard")
    public String userDashboard(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "name") String sortBy,
            @RequestParam(value = "direction", defaultValue = "asc") String direction,
            Model model, Authentication authentication) {

        size = Math.max(size, 1); // Ensure size is at least 1

        String username = getUsernameFromAuthentication(authentication);

        if (username == null) {
            return "redirect:/login"; // Handle case where the user is not authenticated
        }

        User user = userService.findByEmail(username);

        if (user != null) {
            model.addAttribute("user", user);
            Page<Contact> pageContacts = contactService.getAllContactsForUser(user, size, page, sortBy, direction);
            model.addAttribute("contacts", pageContacts);
        } else {
            return "redirect:/login"; // Handle the case where the user is null
        }

        return "user/dashboard";
    }

// User profile
    @RequestMapping(value = "/profile")
    public String userProfile(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "name") String sortBy,
            @RequestParam(value = "direction", defaultValue = "asc") String direction,
            Model model, Authentication authentication) {

        size = Math.max(size, 1); // Ensure size is at least 1

        String username = getUsernameFromAuthentication(authentication);

        if (username == null) {
            return "redirect:/login"; // Handle case where the user is not authenticated
        }

        User user = userService.findByEmail(username);

        if (user != null) {
            model.addAttribute("user", user);
            Page<Contact> pageContacts = contactService.getAllContactsForUser(user, size, page, sortBy, direction);
            model.addAttribute("contacts", pageContacts);
        } else {
            return "redirect:/login"; // Handle the case where the user is null
        }

        return "user/profile";
    }

}
