package ca.gb.comp3095.foodrecipe.events;

import ca.gb.comp3095.foodrecipe.model.domain.Recipe;
import ca.gb.comp3095.foodrecipe.model.domain.User;
import ca.gb.comp3095.foodrecipe.model.repo.RecipeRespository;
import ca.gb.comp3095.foodrecipe.model.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class DummyUserCreation {
    @Autowired
    UserService userService;

    @Autowired
    RecipeRespository recipeRespository;

    User dummyUser;

    @EventListener(ApplicationReadyEvent.class)
    public void createDummyUser() {
        dummyUser = userService.createNewUser(User.builder()
                .name("test")
                .email("test@food-recipe.com")
                .password("test")
                .build());
        recipeRespository.save(Recipe.builder().title("dummy title").description("dummy description").ingredients("onion").instructions("cut onions").cookingTime(Duration.ofMinutes(10)).servings(2L).user(dummyUser).build());
        log.info("User created successfully {}", dummyUser);
    }

    @PreDestroy
    public void removeDummyUser() {
        List<Long> recipeId = recipeRespository.findAllByUserId(dummyUser.getId()).stream().map(Recipe::getId).collect(Collectors.toList());
        recipeRespository.deleteAllById(recipeId);
        userService.deleteUserById(dummyUser.getId());
        log.info("successfully removed user!");
    }
}
