package ca.gb.comp3095.foodrecipe.model.service;

import ca.gb.comp3095.foodrecipe.model.domain.User;

import java.util.Optional;

public interface UserService {
    Optional<User> getUserById(Long id);

    Optional<User> getUserByName(String username);

    User createNewUser(User user);

    User changePassword(Long id, String password);

    void deleteUserById(Long id);

    User saveUser(User user);

}
