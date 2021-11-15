package ca.gb.comp3095.foodrecipe.model.repo;

import ca.gb.comp3095.foodrecipe.model.RecipeTestFactory;
import ca.gb.comp3095.foodrecipe.model.UserTestFactory;
import ca.gb.comp3095.foodrecipe.model.domain.Recipe;
import ca.gb.comp3095.foodrecipe.model.domain.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RecipeRespositoryTest {

    @Autowired
    RecipeRespository recipeRepository;

    @Autowired
    UserRepository userRepository;

    User testUser;

    List<User> allUsers = new ArrayList<>();

    @BeforeEach
    void setUp() {
        testUser = userRepository.save(UserTestFactory.aRandomUser("test-user1"));
        userRepository.save(UserTestFactory.aRandomUser("test-user2"));
        userRepository.findAll().forEach(allUsers::add);
        recipeRepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    void itSavesRecipe() {
        Recipe recipe = RecipeTestFactory.getRecipeForUser(User.builder().id(testUser.getId()).build(), "some recipe", "some details");
        Recipe savedRecipe = recipeRepository.save(recipe);
        assertThat(savedRecipe).isNotNull();
        recipeRepository.deleteAll();
    }

    @Test
    void itGetsAllRecipesForUser() {

        User testUser2 = userRepository.save(UserTestFactory.aRandomUser("test-user-2"));
        List<Recipe> threeRecipes = List.of(
                RecipeTestFactory.getRecipeForUser(testUser2, "3 some recipe", "some details"),
                RecipeTestFactory.getRecipeForUser(testUser2, "3 some recipe 2", "some details 2"),
                RecipeTestFactory.getRecipeForUser(testUser2, "3 some recipe 3", "some details 3")
        );
        recipeRepository.saveAll(threeRecipes);

        List<Recipe> twoRecipes = List.of(
                RecipeTestFactory.getRecipeForUser(testUser, "some recipe", "some details"),
                RecipeTestFactory.getRecipeForUser(testUser, "some recipe 2", "some details 2")
        );

        List<Recipe> testUserRecipes = recipeRepository.saveAll(twoRecipes);
        List<Recipe> allRecipesForUser = recipeRepository.findAllByUserId(testUser.getId());
        assertEquals(2, allRecipesForUser.size());
        assertEquals(twoRecipes.size() + threeRecipes.size(), recipeRepository.findAll().size());
        Optional<Recipe> expectedRecipe = testUserRecipes.stream().findAny();
        assertTrue(expectedRecipe.isPresent());
        assertEquals(expectedRecipe, recipeRepository.findById(expectedRecipe.get().getId()));

        recipeRepository.deleteAll();
    }

    @Test
    void itUpdatesRecipe() {
        Recipe recipe = RecipeTestFactory.getRecipeForUser(User.builder().id(testUser.getId()).build(), "some recipe", "some details");
        Recipe savedRecipe = recipeRepository.save(recipe);
        String oldInstructions = savedRecipe.getInstructions();
        assertNotNull(savedRecipe);

        String newInstructions = "dummy instructions 2 !";
        recipe.setInstructions(newInstructions);
        recipeRepository.save(recipe);
        recipeRepository.flush();

        savedRecipe = recipeRepository.findById(savedRecipe.getId()).get();
        assertEquals(newInstructions, savedRecipe.getInstructions());
        assertNotEquals(oldInstructions, newInstructions);
        recipeRepository.deleteAll();
    }

    @Test
    @Transactional
    void usersCanLikeMultipleRecipes() {
        User firstUser = allUsers.get(0);
        User secondUser = allUsers.get(1);
        Recipe recipe1 = Recipe.builder().user(firstUser).description("recipe1").build();
        Recipe recipe2 = Recipe.builder().user(firstUser).description("recipe2").build();
        Recipe recipe3 = Recipe.builder().user(secondUser).description("recipe3").build();
        User firstUserById = userRepository.findById(firstUser.getId()).get();
        User secondUserById = userRepository.findById(secondUser.getId()).get();
        assertThat(firstUserById.getLikedRecipes()).isNullOrEmpty();
        assertThat(secondUserById.getLikedRecipes()).isNullOrEmpty();
        assertThat(recipeRepository.findAll()).isNullOrEmpty();

        recipe1 = recipeRepository.save(recipe1);
        recipe2 = recipeRepository.save(recipe2);
        recipe3 = recipeRepository.save(recipe3);

        assertThat(recipeRepository.findAll()).isNotEmpty();
        firstUserById = userRepository.findById(firstUser.getId()).get();
        secondUserById = userRepository.findById(secondUser.getId()).get();
        assertThat(firstUserById.getLikedRecipes()).isNullOrEmpty();
        assertThat(secondUserById.getLikedRecipes()).isNullOrEmpty();


        Set likedRecipes = new HashSet<>();
        likedRecipes.add(recipe1);
        secondUser.setLikedRecipes(likedRecipes);
        userRepository.save(secondUser);

        secondUserById = userRepository.findById(secondUser.getId()).get();
        firstUserById = userRepository.findById(firstUser.getId()).get();
        assertThat(secondUserById.getLikedRecipes()).contains(recipe1);
        assertThat(secondUserById.getLikedRecipes()).doesNotContainAnyElementsOf(Set.of(recipe2, recipe3));
        assertThat(firstUserById.getLikedRecipes()).isNullOrEmpty();

        secondUser.getLikedRecipes().add(recipe2);
        userRepository.save(secondUser);
        secondUserById = userRepository.findById(secondUser.getId()).get();
        assertThat(secondUserById.getLikedRecipes()).doesNotContainAnyElementsOf(Set.of(recipe3));
        assertThat(secondUserById.getLikedRecipes()).containsAll(Set.of(recipe1, recipe2));

        assertThat(recipeRepository.findAllByUser(firstUser)).isNotEmpty();
        assertThat(recipeRepository.findAllByUser(secondUser)).contains(recipe3);

        recipe2 = recipeRepository.getById(recipe2.getId());
        assertThat(recipe2.getLikedBy()).isNotEmpty();

        recipeRepository.deleteAll();
    }
}