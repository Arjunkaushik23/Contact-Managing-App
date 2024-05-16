package com.scm.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.scm.entities.Contact;
import com.scm.entities.User;
import com.scm.services.ContactService;
import com.scm.services.UserService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/user")
public class UserController {

    private final ContactService contactService;

    private final UserService userService;

    // user dashboard

    public UserController(ContactService contactService, UserService userService) {
        this.contactService = contactService;
        this.userService = userService;
    }

    @PostMapping("/contacts/add")
    public String addContact(@ModelAttribute("contact") Contact contact, Model model, HttpSession session) {
        // Logic to add the contact to the database
        User user = (User) session.getAttribute("loggedInUser");
        contact.setUser(user);
        contactService.saveContact(contact);

        // Redirect to the dashboard page after adding the contact
        return "redirect:/user/dashboard";
    }

    @RequestMapping(value = "/dashboard")
    public String userDashboard(Model model, HttpSession session) {

        // Assuming authentication is successful
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        // Add the "user" attribute to the model
        model.addAttribute("user", userDetails);

        // Retrieve user's contacts from the database and add them to the model
        User user = (User) session.getAttribute("loggedInUser");
        List<Contact> contacts = contactService.getAllContactsForUser(user);
        model.addAttribute("contacts", contacts);
        return "user/dashboard";
    }

    @PostMapping("/contacts/delete/{id}")
    public String deleteContact(@PathVariable Long id, HttpSession session) {
        // Get the logged-in user from the session
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            // Handle case where user is not logged in
            return "redirect:/login"; // Redirect to login page is here

        }

        // Check if the contact belongs to the logged-in user
        Contact contact = contactService.getContactById(id);
        if (contact == null || !contact.getUser().equals(user)) {
            // Handle case where contact doesn't exist or doesn't belong to the user
            return "redirect:/user/dashboard"; // Redirect to dashboard
        }

        // Delete the contact
        contactService.deleteContactById(id);

        // Redirect to dashboard after deletion
        return "redirect:/user/dashboard";
    }

    // profile page

    // @PostMapping("/addContact")
    // public String addContact(@ModelAttribute Contact contact, Principal
    // principal) {
    // // Set user for the contact from Principal
    // User user = getUserFromPrincipal(principal);
    // contact.setUser(user);
    // contactService.saveContact(contact);
    // return "redirect:/user/dashboard";
    // }

    @RequestMapping(value = "/profile")
    public String userProfile() {
        return "user/profile";
    }

    // public User getUserFromPrincipal(Principal principal) {
    // if (principal == null) {
    // return null;
    // }

    // // Extract username from Principal
    // String username = principal.getName();

    // // Fetch User from UserService based on username
    // return userService.findByUsername(username);
    // }

    // user add contacts page

    // user view contacts

}
