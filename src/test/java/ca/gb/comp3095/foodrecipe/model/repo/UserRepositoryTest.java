package ca.gb.comp3095.foodrecipe.model.repo;

import ca.gb.comp3095.foodrecipe.model.UserTestFactory;
import ca.gb.comp3095.foodrecipe.model.domain.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserRepositoryTest {

    @Autowired
    UserRepository userRepository;

    private User user;

    @BeforeEach
    void setUp() {
        user = userRepository.save(UserTestFactory.aRandomUser("test-user"));
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    void itLoadsUser() {
        Assertions.assertEquals(Optional.of(user), userRepository.findByName(user.getName()));
        Assertions.assertNotNull(user.getCreationTime());
    }
}