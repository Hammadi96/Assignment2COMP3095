package ca.gb.comp3095.foodrecipe.events;

import ca.gb.comp3095.foodrecipe.model.domain.User;
import ca.gb.comp3095.foodrecipe.model.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;

@Component
@Slf4j
public class DummyUserCreation {
    @Autowired
    UserService userService;

    User dummyUser;

    @EventListener(ApplicationReadyEvent.class)
    public void createDummyUser() {
        dummyUser = userService.createNewUser(User.builder()
                .name("test")
                .email("test@food-recipe.com")
                .password("test")
                .build());
        log.info("User created successfully {}", dummyUser);
    }

    @PreDestroy
    public void removeDummyUser() {
        userService.deleteUserById(dummyUser.getId());
        log.info("successfully removed user!");
    }
}
