package ca.gb.comp3095.foodrecipe.model;

import ca.gb.comp3095.foodrecipe.model.domain.Recipe;
import ca.gb.comp3095.foodrecipe.model.domain.User;

public class RecipeTestFactory {

    public static Recipe getRecipeForUser(User user, String title, String description) {
        return Recipe.builder()
                .user(user)
                .title(title)
                .description(description)
                .build();
    }
}
