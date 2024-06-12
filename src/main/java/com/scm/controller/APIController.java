package com.scm.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.scm.entities.Contact;
import com.scm.services.ContactService;

@RestController
public class APIController {


    private final ContactService contactService;

    public APIController(ContactService contactService) {
        this.contactService = contactService; 
    }



    //GET THE CONTACTS
    @GetMapping("/contacts/{contactId}")
    public Contact getContact(@PathVariable Long contactId){

        return contactService.getContactById(contactId);

    }

}
