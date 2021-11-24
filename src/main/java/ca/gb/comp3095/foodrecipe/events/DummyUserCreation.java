package ca.gb.comp3095.foodrecipe.events;

import ca.gb.comp3095.foodrecipe.controller.recipe.RecipeConverter;
import ca.gb.comp3095.foodrecipe.controller.recipe.RecipeDto;
import ca.gb.comp3095.foodrecipe.model.domain.Recipe;
import ca.gb.comp3095.foodrecipe.model.domain.User;
import ca.gb.comp3095.foodrecipe.model.repo.RecipeRespository;
import ca.gb.comp3095.foodrecipe.model.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class DummyUserCreation {
    @Autowired
    UserService userService;

    @Value("classpath:recipe-list.json")
    Resource resourceFile;

    @Autowired
    RecipeRespository recipeRespository;

    @Autowired
    ObjectMapper objectMapper;


    User dummyUser;

    @EventListener(ApplicationReadyEvent.class)
    public void createDummyUser() {
        dummyUser = userService.createNewUser(User.builder()
                .name("testUser")
                .email("test@food-recipe.com")
                .password("test")
                .build());
        recipeRespository.save(Recipe.builder().title("dummy title").description("dummy description").ingredients("onion").instructions("cut onions").cookingTime(Duration.ofMinutes(10)).servings(2L).user(dummyUser).build());
        // checking recipe resource
        if (resourceFile != null) {
            log.info("resource file {}", resourceFile.getFilename());
        }
        try {
            loadRecipes(resourceFile.getFile());
        } catch (Exception e) {
            log.warn("unable to load recipes", e);
        }
        log.info("User created successfully {}", dummyUser);
    }

    public void loadRecipes(File file) throws IOException {
        RecipeDto[] recipes = objectMapper.readValue(file, RecipeDto[].class);
        List<Recipe> recipeList = Arrays.stream(recipes)
                .map(RecipeConverter::toDomain)
                .collect(Collectors.toList());
        recipeList.forEach(r -> r.setUser(dummyUser));
        recipeRespository.saveAll(recipeList);
    }

    @PreDestroy
    public void removeDummyUser() {
        List<Long> recipeId = recipeRespository.findAllByUserId(dummyUser.getId()).stream().map(Recipe::getId).collect(Collectors.toList());
        recipeRespository.deleteAllById(recipeId);
        userService.deleteUserById(dummyUser.getId());
        log.info("successfully removed user!");
    }
}
