package com.scm.services;

import java.util.List;

import com.scm.entities.Contact;
import com.scm.entities.User;

public interface ContactService {

    List<Contact> getAllContactsForUser(User user);

    // void saveContact(Contact contact, User user);
    public Contact saveContact(Contact contact);

    Contact updateContact(Contact contact);

    List<Contact> allContacts();

    void deleteContactById(Long contactId);

    Contact getContactById(Long contactId);
    
    List<Contact> search(String name, String email, String phoneNumber);

}
