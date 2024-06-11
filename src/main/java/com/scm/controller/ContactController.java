package com.scm.controller;

import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.scm.entities.Contact;
import com.scm.entities.User;
import com.scm.forms.ContactForm;
import com.scm.helpers.AppConstants;
import com.scm.helpers.Message;
import com.scm.helpers.MessageType;
import com.scm.services.ContactService;
import com.scm.services.ImageService;
import com.scm.services.UserService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/user/contacts")
public class ContactController {

    public static final String DASHBOAR_REDIRECT_URL = "redirect:/user/dashboard";

    public static final Logger LOGGER = LoggerFactory.getLogger(ContactController.class);

    private final ContactService contactService;
    private final UserService userService;
    private final ImageService imageService;

    private User byEmail;

    public ContactController(ContactService contactService, UserService userService, ImageService imageService) {
        this.contactService = contactService;
        this.userService = userService;
        this.imageService = imageService;
    }

    @RequestMapping("/add")
    public String addContact(
            Model model
    // @ModelAttribute("contact") Contact contact, Model model, Authentication
    // authentication
    ) {
        // String username = getUsernameFromAuthentication(authentication);

        // if (username != null) {
        // User user = userService.findByEmail(username);
        // if (user != null) {
        // Set the user for the contact and save
        // contact.setUser(user);
        // contactService.saveContact(contact, user);
        // Redirect to the dashboard page after adding the contact
        // return "user/add_contact";
        // return DASHBOAR_REDIRECT_URL;
        // } else {
        // Handle case where user is not found
        // LOGGER.error("User not found for username: " + username);
        // }
        // }
        // Redirect to an error page if user is not found or username is null
        ContactForm contactForm = new ContactForm();
        // contactForm.setName("Arjun Kaushik");
        // contactForm.setEmail("arjun9717@gmail.com");
        // contactForm.setFavorite(true);
        model.addAttribute("contactForm", contactForm);

        return "user/add_contact";
    }

    @RequestMapping(value = "/do-add", method = RequestMethod.POST)
    public String saveContact(@ModelAttribute("contactForm") @Valid ContactForm contactForm,
            BindingResult result,
            Authentication authentication,
            // Model model,
            HttpSession session) {

        // validate the form
        if (result.hasErrors()) {
            session.setAttribute("message", Message.builder()
                    .content("Please correct the following errors")
                    .type(MessageType.red)
                    .build());
            return "user/add_contact";
        }

        String username = getUsernameFromAuthentication(authentication);

        //image process
        LOGGER.info("file information : {}", contactForm.getContactImage().getOriginalFilename());

        String filename = UUID.randomUUID().toString();

        // upload file
        String fileURL = imageService.uploadImage(contactForm.getContactImage(), filename);

        // change form to contact
        User user = userService.findByEmail(username);

        // process the contact picture
        Contact contact = new Contact();
        contact.setName(contactForm.getName());
        contact.setEmail(contactForm.getEmail());
        contact.setAddress(contactForm.getAddress());
        contact.setDescription(contactForm.getDescription());
        contact.setFavorite(contactForm.isFavorite());
        contact.setPhoneNumber(contactForm.getPhoneNumber());
        contact.setLinkedInLink(contactForm.getLinkedInLink());
        contact.setWebsiteLink(contactForm.getWebsiteLink());
        contact.setPicture(fileURL);
        contact.setCloudinaryImagePublicId(filename);
        // contact.setPicture(null);
        contact.setUser(user);

        // process the data
        contactService.saveContact(contact);

        Message message = Message.builder().content("Contact Added Successfully").type(MessageType.blue).build();

        session.setAttribute("message", message);

        // print that thing
        System.out.println(contactForm);

        return "redirect:/user/contacts/add";
    }

    @DeleteMapping("/contacts/delete/{id}")
    public String deleteContact(@PathVariable Long id, HttpSession session, Authentication authentication) {
        String username = getUsernameFromAuthentication(authentication);

        if (username == null) {
            // Handle case where the user is not logged in
            return "redirect:/login"; // Redirect to login page
        }

        User user = userService.findByEmail(username);

        if (user == null) {
            // Handle case where user is not found
            return "redirect:/login"; // Redirect to login page
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
                LOGGER.info("User attribute for login is: " + username);
            }
        }

        return username;
    }

    @RequestMapping()
    public String viewContacts(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "name") String sortBy,
            @RequestParam(value = "direction", defaultValue = "asc") String direction,
            Model model, Authentication authentication) {

        size = Math.max(size, 1); // Ensure size is at least 1

        String username = getUsernameFromAuthentication(authentication);
        LOGGER.info("The username is : {}", username);

        if (username == null) {
            return "redirect:/login"; // Handle case where the user is not authenticated
        }

        User userByEmail = userService.findByEmail(username);
        LOGGER.info("The user is following : {}", userByEmail);

        if (userByEmail != null) {
            // Load all contacts for the user
            Page<Contact> pageContact = contactService.getAllContactsForUser(userByEmail, size, page, sortBy, direction);

            // Log the contacts retrieved
            if (pageContact != null && !pageContact.isEmpty()) {
                pageContact.forEach(contact -> LOGGER.info("Contact: {}", contact));
            } else {
                LOGGER.info("No contacts found for user: {}", username);
            }

            // LOGGER.info("Retrieved contacts for user {}:", username);

            // for (Contact contact : pageContact) {
            //     LOGGER.info("Here are the information {}:", contact.toString());
            // }

            model.addAttribute("pageContact", pageContact);
            model.addAttribute("pageSize", AppConstants.PAGE_SIZE);

        } else {
            return "redirect:/login"; // Handle the case where the user is null
        }

        return "user/contacts";
    }

    // search handler

    @RequestMapping("/search")
    public String searchHandler(
        @RequestParam("field") String field,
        @RequestParam("keyword") String keywword
    ){

        LOGGER.info("feild {} keyword {}", field, keywword);
        return "user/search";
    }


}
