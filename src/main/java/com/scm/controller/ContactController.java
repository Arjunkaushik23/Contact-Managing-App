package com.scm.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.scm.entities.Contact;
import com.scm.entities.User;
import com.scm.services.ContactService;
import com.scm.services.UserService;

import jakarta.servlet.http.HttpSession;

@Controller
public class ContactController {

    public static final String DASHBOAR_REDIRECT_URL = "redirect:/user/dashboard";

    public static final Logger LOGGER = LoggerFactory.getLogger(ContactController.class);

    private final ContactService contactService;
    private final UserService userService;

    public ContactController(ContactService contactService, UserService userService) {
        this.contactService = contactService;
        this.userService = userService;
    }

    @PostMapping("/contacts/add")
    public String addContact(@ModelAttribute("contact") Contact contact, Model model, Authentication authentication) {
        // Get the authenticated user from Authentication
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userService.findByEmail(userDetails.getUsername());

        LOGGER.info("This is the user which we are trying to get : " + userDetails.getUsername());

        if (user != null) {
            // Set the user for the contact and save
            contact.setUser(user);
            contactService.saveContact(contact, user);
        } else {
            // Handle case where user is not found
            LOGGER.error("User not found for username: " + userDetails.getUsername());
            // You can redirect to an error page or handle it as per your application's
            // logic
            return "redirect:/error";
        }

        // Redirect to the dashboard page after adding the contact
        return DASHBOAR_REDIRECT_URL;
    }

    @DeleteMapping("/contacts/delete/{id}")
    public String deleteContact(@PathVariable Long id, HttpSession session, Authentication authentication) {
        // Get the logged-in user from the session
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userService.findByEmail(userDetails.getUsername());

        if (user == null) {
            // Handle case where user is not logged in
            return "redirect:/login"; // Redirect to login page is here

        }

        // Check if the contact belongs to the logged-in user
        Contact contact = contactService.getContactById(id);
        if (contact == null || !contact.getUser().equals(user)) {
            // Handle case where contact doesn't exist or doesn't belong to the user
            return DASHBOAR_REDIRECT_URL; // Redirect to dashboard
        }

        // Delete the contact
        contactService.deleteContactById(id);

        // Redirect to dashboard after deletion
        return DASHBOAR_REDIRECT_URL;
    }

}
