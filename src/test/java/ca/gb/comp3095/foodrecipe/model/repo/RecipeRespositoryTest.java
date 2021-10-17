package ca.gb.comp3095.foodrecipe.model.repo;

import ca.gb.comp3095.foodrecipe.model.RecipeTestFactory;
import ca.gb.comp3095.foodrecipe.model.UserTestFactory;
import ca.gb.comp3095.foodrecipe.model.domain.Recipe;
import ca.gb.comp3095.foodrecipe.model.domain.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RecipeRespositoryTest {

    @Autowired
    RecipeRespository recipeRespository;

    @Autowired
    UserRepository userRepository;

    User testUser;

    @BeforeEach
    void setUp() {
        testUser = userRepository.save(UserTestFactory.aRandomUser("test-user"));
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    void itSavesRecipe() {
        Recipe recipe = RecipeTestFactory.getRecipeForUser(User.builder().id(testUser.getId()).build(), "some recipe", "some details");
        Recipe savedRecipe = recipeRespository.save(recipe);
        Assertions.assertNotNull(savedRecipe);
        recipeRespository.deleteAll();
    }

    @Test
    void itGetsAllRecipesForUser() {

        User testUser2 = userRepository.save(UserTestFactory.aRandomUser("test-user-2"));
        List<Recipe> threeRecipes = List.of(
                RecipeTestFactory.getRecipeForUser(testUser2, "3 some recipe", "some details"),
                RecipeTestFactory.getRecipeForUser(testUser2, "3 some recipe 2", "some details 2"),
                RecipeTestFactory.getRecipeForUser(testUser2, "3 some recipe 3", "some details 3")
        );
        recipeRespository.saveAll(threeRecipes);

        List<Recipe> twoRecipes = List.of(
                RecipeTestFactory.getRecipeForUser(testUser, "some recipe", "some details"),
                RecipeTestFactory.getRecipeForUser(testUser, "some recipe 2", "some details 2")
        );

        List<Recipe> testUserRecipes = recipeRespository.saveAll(twoRecipes);
        List<Recipe> allRecipesForUser = recipeRespository.findAllByUserId(testUser.getId());
        Assertions.assertEquals(2, allRecipesForUser.size());
        Assertions.assertEquals(twoRecipes.size() + threeRecipes.size(), recipeRespository.findAll().size());
        Optional<Recipe> expectedRecipe = testUserRecipes.stream().findAny();
        Assertions.assertTrue(expectedRecipe.isPresent());
        Assertions.assertEquals(expectedRecipe, recipeRespository.findById(expectedRecipe.get().getId()));

        recipeRespository.deleteAll();
    }
}