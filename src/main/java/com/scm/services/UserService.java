package com.scm.services;

import java.util.List;
import java.util.Optional;

import com.scm.entities.User;

public interface UserService {

    User saveUser(User user);

    Optional<User> getById(String id);

    Optional<User> updateuser(User user);

    void deleteUser(String id);

    boolean isUserExist(String userId);

    boolean isUserExistByEmail(String email);

    List<User> getAllUsers();

    User findByUsername(String username);

    User findByEmail(String email);

    // add more methods if needed
}
