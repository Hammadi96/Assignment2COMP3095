package ca.gb.comp3095.foodrecipe.model;

import ca.gb.comp3095.foodrecipe.model.domain.Ingredient;
import ca.gb.comp3095.foodrecipe.model.domain.Recipe;
import ca.gb.comp3095.foodrecipe.model.domain.User;

import java.util.HashSet;
import java.util.Set;

public class RecipeTestFactory {

    public static Recipe getRecipeForUser(User user, String title, String description) {
        Set<Ingredient> ingredientSet = new HashSet<>();
        ingredientSet.add(Ingredient.builder().name("title").build());
        return Recipe.builder()
                .user(user)
                .title(title)
                .description(description)
                .allIngredients(ingredientSet)
                .build();
    }
}
