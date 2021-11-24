package ca.gb.comp3095.foodrecipe.model.service;

import ca.gb.comp3095.foodrecipe.model.domain.Recipe;

import java.util.List;
import java.util.Optional;

public interface RecipeService {
    Recipe createRecipe(Recipe recipe);

    Optional<Recipe> getRecipeById(Long id);

    Recipe updateRecipe(Recipe recipe);

    List<Recipe> getAllRecipesForUser(Long userId);

    default boolean deleteRecipeById(Long id) {
        return false;
    }
}
