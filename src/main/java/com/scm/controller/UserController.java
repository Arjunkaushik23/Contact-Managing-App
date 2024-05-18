package com.scm.controller;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.scm.entities.Contact;
import com.scm.entities.User;
import com.scm.services.ContactService;
import com.scm.services.UserService;

@Controller
@RequestMapping("/user")
public class UserController {

    private final ContactService contactService;

    private final UserService userService;

    // User dashboard
    public UserController(ContactService contactService, UserService userService) {
        this.contactService = contactService;
        this.userService = userService;
    }

    @RequestMapping(value = "/dashboard")
    public String userDashboard(Model model, Authentication authentication) {
        User user = null;
        if (authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            user = userService.findByEmail(userDetails.getUsername());
        } else if (authentication.getPrincipal() instanceof DefaultOAuth2User) {
            DefaultOAuth2User oauthUser = (DefaultOAuth2User) authentication.getPrincipal();
            String username = oauthUser.getAttribute("email"); // Make sure to use the correct attribute key
            user = userService.findByEmail(username);
        }

        if (user != null) {
            model.addAttribute("user", user);
            List<Contact> contacts = contactService.getAllContactsForUser(user);
            model.addAttribute("contacts", contacts);
        } else {
            // Handle the case where the user is null
            // Redirect to a default page or show an error message
            return "redirect:/some-default-page";
        }
        return "user/dashboard";
    }

    @RequestMapping(value = "/profile")
    public String userProfile(Model model, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        model.addAttribute("user", userDetails);
        User user = userService.findByEmail(userDetails.getUsername());
        List<Contact> contacts = contactService.getAllContactsForUser(user);
        model.addAttribute("contacts", contacts);

        return "user/profile";
    }

}
