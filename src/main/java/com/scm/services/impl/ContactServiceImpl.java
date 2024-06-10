package com.scm.services.impl;

import com.scm.entities.Contact;
import com.scm.entities.User;
import com.scm.repositories.ContactRepository;
import com.scm.services.ContactService;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

@Service
public class ContactServiceImpl implements ContactService {

    private final ContactRepository contactRepository;
    private final Logger logger = LoggerFactory.getLogger(ContactServiceImpl.class);

    public ContactServiceImpl(ContactRepository contactRepository) {
        this.contactRepository = contactRepository;
    }

    // @Override
    // public void saveContact(Contact contact, User user) {
    //     contact.setUser(user);
    //     contactRepository.save(contact);
    // }
    @Override
    public Contact saveContact(Contact contact) {
        return contactRepository.save(contact);
    }

    @Override
    public void deleteContactById(Long contactId) {
        contactRepository.deleteById(contactId);
    }

    @Override
    public Contact getContactById(Long contactId) {
        return contactRepository.findById(contactId).orElse(null);
    }

    @Override
    public Contact updateContact(Contact updatedContact) {

        List<Contact> contacts = new ArrayList<>();

        for (Contact contact : contacts) {
            if (contact.getId().equals(updatedContact.getId())) {
                contact.setName(updatedContact.getName());
                contact.setEmail(updatedContact.getEmail());
                contact.setPhoneNumber(updatedContact.getPhoneNumber());
                contact.setAddress(updatedContact.getAddress());
                contact.setPicture(updatedContact.getPicture());
                contact.setDescription(updatedContact.getDescription());
                contact.setFavorite(updatedContact.isFavorite());
                contact.setWebsiteLink(updatedContact.getWebsiteLink());
                contact.setLinkedInLink(updatedContact.getLinkedInLink());
                contact.setUser(updatedContact.getUser());
                contact.setSocialLinks(updatedContact.getSocialLinks());
                return contact;
            }
        }
        throw new IllegalArgumentException("Contact with ID " + updatedContact.getId() + " not found.");
    }

    @Override
    public List<Contact> allContacts() {
        return contactRepository.findAll();
    }

    @Override
    public List<Contact> search(String name, String email, String phoneNumber) {

        List<Contact> contacts = new ArrayList<>();
        return contacts.stream()
                .filter(contact -> (name == null || contact.getName().contains(name))
                && (email == null || contact.getEmail().contains(email))
                && (phoneNumber == null || contact.getPhoneNumber().contains(phoneNumber)))
                .collect(Collectors.toList());
    }

    @Override
    public Page<Contact> getAllContactsForUser(User user, int size, int page, String sortField, String sortDirection) {


        logger.info("getAllContactsForUser called with size: {}, page: {}, sortField: {}, sortDirection: {}",
        size, page, sortField, sortDirection);

        // Validate the page size
        if (size < 1) {
            throw new IllegalArgumentException("Page size must not be less than one");
        }

        Sort sort = sortDirection.equals("desc")
                ? Sort.by(sortField).descending()
                : Sort.by(sortField).ascending();

        var pageable = PageRequest.of(page, size, sort);

        return contactRepository.findByUser(user, pageable);
    }

    // @Override
    // public Page<Contact> getAllContactsForUser(User user, int size, int page) {
    //     throw new UnsupportedOperationException("Not supported yet.");
    // }
}
