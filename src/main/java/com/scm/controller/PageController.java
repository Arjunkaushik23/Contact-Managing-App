package com.scm.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.scm.entities.User;
import com.scm.forms.UserForm;
import com.scm.helpers.Message;
import com.scm.helpers.MessageType;
import com.scm.services.UserService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    private UserService userService;

    public PageController(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping("/home")
    public String getHome(Model model) {
        System.out.println("Home page handler");
        model.addAttribute("name", "SubString Technologies");
        model.addAttribute("channel", "Arjun youtube page");
        return "home";
    }

    @GetMapping("/")
    public String index() {
        return "redirect:/home";
    }

    // about route
    @RequestMapping("/about")
    public String aboutpage() {
        return "about";
    }

    // services

    @RequestMapping("/service")
    public String servicePage() {
        return "service";
    }

    @RequestMapping("/contact")
    public String contactPage() {
        return "contact";
    }

    @RequestMapping("/login")
    public String loginPage() {
        return "login";
    }

    @RequestMapping("/signup")
    public String signupPage(Model model) {
        UserForm userForm = new UserForm();
        // we can do the default data as well
        model.addAttribute("userForm", userForm);
        return "signup";
    }

    @RequestMapping(value = "/do-register", method = RequestMethod.POST)
    public String requestMethodName(@Valid @ModelAttribute UserForm userForm, BindingResult bindingResult,
            HttpSession session) {

        // fetch form data

        // UserForm

        // System.out.println(userForm);
        // validate form data
        if (bindingResult.hasErrors()) {
            return "signup";
        }

        // save to database

        // UserForm ----> user

        // User user = User.builder()
        // .name(userForm.getName())
        // .email(userForm.getEmail())
        // .password(userForm.getPassword())
        // .about(userForm.getAbout())
        // .phoneNumber(userForm.getPhoneNumber())
        // .profilePic("https://www.pngall.com/wp-content/uploads/5/Profile-Avatar-PNG.png")
        // .build();

        User user = new User();
        user.setName(userForm.getName());
        user.setEmail(userForm.getEmail());
        user.setPassword(userForm.getPassword());
        user.setAbout(userForm.getAbout());
        user.setPhoneNumber(userForm.getPhoneNumber());
        user.setProfilePic("https://www.pngall.com/wp-content/uploads/5/Profile-Avatar-PNG.png");

        User savedUser = userService.saveUser(user);

        System.out.println("User saved successfully");
        // message= "Registeration successful"
        Message message = Message.builder().content("Registration Successfull").type(MessageType.blue).build();
        // add the session
        session.setAttribute("message", message);
        // redirecting to login page

        return "redirect:/signup";
    }

}
