package ca.gb.comp3095.foodrecipe.model.service;

import ca.gb.comp3095.foodrecipe.model.RecipeTestFactory;
import ca.gb.comp3095.foodrecipe.model.UserTestFactory;
import ca.gb.comp3095.foodrecipe.model.domain.Recipe;
import ca.gb.comp3095.foodrecipe.model.domain.User;
import ca.gb.comp3095.foodrecipe.model.repo.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RecipeServiceIntTest {

    @Autowired
    UserRepository userRepository;

    User testUser;

    @Autowired
    RecipeService recipeService;

    @BeforeEach
    void setUp() {
        testUser = userRepository.save(UserTestFactory.aRandomUser("test-user1"));
        userRepository.save(UserTestFactory.aRandomUser("test-user2"));
    }

    @AfterEach
    void tearDown() {
        recipeService.getAllRecipesForUser(testUser.getId())
                .forEach(r -> recipeService.deleteRecipeById(r.getId()));
        userRepository.delete(testUser);
    }

    @Test
    void itLoads() {
        Recipe recipeForUser = RecipeTestFactory.getRecipeForUser(testUser, "title", "description");
        Recipe savedRecipe = recipeService.createRecipe(recipeForUser);
        assertThat(savedRecipe).isNotNull();
        recipeService.deleteRecipeById(savedRecipe.getId());
    }
}