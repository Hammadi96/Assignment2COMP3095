package ca.gb.comp3095.foodrecipe.model.service;

import ca.gb.comp3095.foodrecipe.controller.recipe.SearchRecipeCommand;
import ca.gb.comp3095.foodrecipe.model.RecipeTestFactory;
import ca.gb.comp3095.foodrecipe.model.UserTestFactory;
import ca.gb.comp3095.foodrecipe.model.domain.Recipe;
import ca.gb.comp3095.foodrecipe.model.domain.User;
import ca.gb.comp3095.foodrecipe.model.repo.RecipeRespository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SearchServiceImplTest {

    @Autowired
    SearchService searchService;

    @Autowired
    UserService userService;

    @Autowired
    RecipeService recipeService;

    @Autowired
    RecipeRespository recipeRespository;

    User user;

    @BeforeEach
    void setUp() {
        user = userService.createNewUser(UserTestFactory.aRandomUser("test-user"));
    }

    @AfterEach
    void tearDown() {
        recipeRespository.deleteAll();
        userService.deleteUserById(user.getId());
    }

    @Test
    void searchFindsSimilarTitles() {
        Recipe recipe1 = RecipeTestFactory.getRecipeForUser(user, "Onion", "Some onions");
        Recipe recipe2 = RecipeTestFactory.getRecipeForUser(user, "Garlic", "Some garlic");
        Recipe recipe3 = RecipeTestFactory.getRecipeForUser(user, "Onion & Garlic", "Onions and garlic");
        Recipe recipe4 = RecipeTestFactory.getRecipeForUser(user, "Mushroom", "Mushrooms");

        recipe1 = recipeService.createRecipe(recipe1);
        recipe2 = recipeService.createRecipe(recipe2);
        recipe3 = recipeService.createRecipe(recipe3);
        recipe4 = recipeService.createRecipe(recipe4);

        SearchRecipeCommand searchRecipeCommand = SearchRecipeCommand.builder().title("onions").build();

        List<Recipe> recipeList = searchService.findAllBy(searchRecipeCommand);
        Assertions.assertThat(recipeList).isNotEmpty();
        Assertions.assertThat(recipeList).hasSize(2);

        searchRecipeCommand = SearchRecipeCommand.builder().description("onion").build();
        recipeList = searchService.findAllBy(searchRecipeCommand);
        Assertions.assertThat(recipeList).isNotEmpty();
        Assertions.assertThat(recipeList).hasSize(2);

        searchRecipeCommand = SearchRecipeCommand.builder().description("lkj;alkdjfa;lkdjf").build();
        recipeList = searchService.findAllBy(searchRecipeCommand);
        Assertions.assertThat(recipeList).isEmpty();


    }
}