package com.scm.services;

import java.util.List;

import com.scm.entities.Contact;
import com.scm.entities.User;

public interface ContactService {

    List<Contact> getAllContactsForUser(User user);

    void saveContact(Contact contact);

    void deleteContactById(Long contactId);

    Contact getContactById(Long contactId);

}
