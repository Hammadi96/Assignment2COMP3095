package ca.gb.comp3095.foodrecipe.model.service;

import ca.gb.comp3095.foodrecipe.model.domain.User;
import ca.gb.comp3095.foodrecipe.model.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;

    @Override
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public Optional<User> getUserByName(String username) {
        return userRepository.findByName(username);
    }

    @Override
    public User createNewUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public User changePassword(Long id, String password) { // TODO
        return userRepository.findById(id).map(user -> {
            user.setPassword(password);
            return userRepository.save(user);
        }).orElse(null);
    }

    @Override
    public void deleteUserById(Long id) {
        userRepository.deleteById(id);
    }
}
