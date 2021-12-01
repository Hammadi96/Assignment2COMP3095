package ca.gb.comp3095.foodrecipe.model.repo;

import ca.gb.comp3095.foodrecipe.model.RecipeTestFactory;
import ca.gb.comp3095.foodrecipe.model.UserTestFactory;
import ca.gb.comp3095.foodrecipe.model.domain.Recipe;
import ca.gb.comp3095.foodrecipe.model.domain.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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

    @Autowired
    SessionFactory sessionFactory;

    @BeforeEach
    void setUp() {
        testUser = userRepository.save(UserTestFactory.aRandomUser("test-user1"));
        userRepository.save(UserTestFactory.aRandomUser("test-user2"));
        userRepository.findAll().forEach(allUsers::add);
    }

    @AfterEach
    void tearDown() {
        recipeRepository.deleteAll();
        recipeRepository.flush();
        userRepository.deleteAll();
        allUsers.clear();
    }

    @Test
    void itSavesRecipe() {
        Recipe recipe = RecipeTestFactory.getRecipeForUser(User.builder().id(testUser.getId()).build(), "some recipe", "some details");
        Recipe savedRecipe = recipeRepository.save(recipe);
        assertThat(savedRecipe).isNotNull();
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

        savedRecipe = recipeRepository.findById(savedRecipe.getId()).get();
        assertEquals(newInstructions, savedRecipe.getInstructions());
        assertNotEquals(oldInstructions, newInstructions);
    }

    @Test
    void usersCanLikeMultipleRecipes() {
        User firstUser = allUsers.get(0);
        User secondUser = allUsers.get(1);
        Recipe recipe1 = Recipe.builder().user(firstUser).description("recipe1").build();
        Recipe recipe2 = Recipe.builder().user(firstUser).description("recipe2").build();
        Recipe recipe3 = Recipe.builder().user(secondUser).description("recipe3").build();

        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();

        User firstUserById = userRepository.findById(firstUser.getId()).get();
        User secondUserById = userRepository.findById(secondUser.getId()).get();
        assertThat(firstUserById.getLikedRecipes()).isNullOrEmpty();
        assertThat(secondUserById.getLikedRecipes()).isNullOrEmpty();

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

        recipe2 = recipeRepository.findById(recipe2.getId()).get();
        assertThat(recipe2.getLikedBy()).isNotEmpty();

        firstUser.setLikedRecipes(null);
        secondUser.setLikedRecipes(null);
        userRepository.save(firstUser);
        userRepository.save(secondUser);
        recipeRepository.deleteAll();
        recipeRepository.flush();
        transaction.commit();
        session.close();
    }

    @Test
    void likeRecipesAreVisibleByUserAndRecipe() {
        User firstUser = allUsers.get(0);
        User secondUser = allUsers.get(1);
        Recipe recipe1 = Recipe.builder().user(firstUser).description("recipe1").build();
        Recipe recipe2 = Recipe.builder().user(firstUser).description("recipe2").build();
        Recipe recipe3 = Recipe.builder().user(secondUser).description("recipe3").build();
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        recipe1 = recipeRepository.save(recipe1);
        recipe2 = recipeRepository.save(recipe2);
        recipe3 = recipeRepository.save(recipe3);

        firstUser.setLikedRecipes(new HashSet<>());
        firstUser.getLikedRecipes().add(recipe1);
        firstUser.getLikedRecipes().add(recipe2);

        userRepository.save(firstUser);

        firstUser = userRepository.findById(firstUser.getId()).get();
        assertThat(firstUser.getLikedRecipes()).contains(recipe1, recipe2);
        assertThat(firstUser.getLikedRecipes()).doesNotContain(recipe3);

        recipe1 = session.find(Recipe.class, recipe1.getId());
        recipe2 = session.find(Recipe.class, recipe2.getId());
        recipe3 = session.find(Recipe.class, recipe3.getId());

        assertThat(recipe1.getLikedBy()).isNotNull();
        assertThat(recipe1.getLikedBy()).contains(firstUser);
        assertThat(recipe2.getLikedBy()).contains(firstUser);
        assertThat(recipe3.getLikedBy()).isNullOrEmpty();

        firstUser = session.find(User.class, firstUser.getId());
        secondUser = session.find(User.class, secondUser.getId());
        firstUser.setLikedRecipes(null);
        secondUser.setLikedRecipes(null);
        userRepository.save(firstUser);
        userRepository.save(secondUser);
        recipeRepository.deleteAll();

        transaction.commit();
        session.close();
    }
}