package com.scm.services;

import java.util.List;

import org.springframework.data.domain.Page;

import com.scm.entities.Contact;
import com.scm.entities.User;


public interface ContactService {

    Page<Contact> getAllContactsForUser(User user, int size, int page, String sortBy, String sortDirection);

    // void saveContact(Contact contact, User user);
    public Contact saveContact(Contact contact);

    Contact updateContact(Contact contact);

    List<Contact> allContacts();

    void deleteContactById(Long contactId);

    Contact getContactById(Long contactId);
    
    List<Contact> search(String name, String email, String phoneNumber);

}
