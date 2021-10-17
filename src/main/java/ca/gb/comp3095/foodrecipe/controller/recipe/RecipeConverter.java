package ca.gb.comp3095.foodrecipe.controller.recipe;

import ca.gb.comp3095.foodrecipe.model.domain.Recipe;
import ca.gb.comp3095.foodrecipe.model.domain.User;

public class RecipeConverter {

    public static Recipe toDomain(final RecipeDto recipeDto) {
        return Recipe.builder()
                .id(recipeDto.getId())
                .title(recipeDto.getTitle())
                .description(recipeDto.getDescription()).user(User.builder().id(recipeDto.getUserId()).build()).build();
    }

    public static RecipeDto toDto(final Recipe recipe) {
        return RecipeDto.builder()
                .creationTime(recipe.getCreationTime())
                .id(recipe.getId()).title(recipe.getTitle())
                .description(recipe.getDescription())
                .userId(recipe.getUser().getId()).build();
    }
}
