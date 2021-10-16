package ca.gb.comp3095.foodrecipe.model.service;

import ca.gb.comp3095.foodrecipe.model.UserTestFactory;
import ca.gb.comp3095.foodrecipe.model.domain.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserServiceTest {

    @Autowired
    UserService userService;

    @Test
    void itCreatesNewUser() {
        User savedUser = userService.createNewUser(UserTestFactory.aRandomUser("random-user"));
        Assertions.assertNotNull(savedUser);
        Assertions.assertEquals(Optional.of(savedUser), userService.getUserByName(savedUser.getName()));
    }

    @Test
    void itCannotFindUser() {
        Optional<User> userById = userService.getUserById(123L);
        Assertions.assertTrue(userById.isEmpty());
    }
}