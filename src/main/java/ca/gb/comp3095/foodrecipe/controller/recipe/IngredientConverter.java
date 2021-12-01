package ca.gb.comp3095.foodrecipe.controller.recipe;

import ca.gb.comp3095.foodrecipe.model.domain.Ingredient;
import ca.gb.comp3095.foodrecipe.model.domain.Recipe;
import ca.gb.comp3095.foodrecipe.model.domain.User;

public class IngredientConverter {

    public static Ingredient toDomain(final IngredientDto ingredientDto) {
        return Ingredient.builder()
                .name(ingredientDto.getName())
                .recipe(Recipe.builder().id(ingredientDto.getRecipeId()).user(User.builder().id(ingredientDto.getId()).build()).build())
                .build();
    }

    public static IngredientDto toDto(final Ingredient ingredient) {
        return IngredientDto.builder()
                .creationTime(ingredient.getCreationTime())
                .lastModified(ingredient.getModificationTime())
                .id(ingredient.getId())
                .recipeId(ingredient.getRecipe().getId())
                .name(ingredient.getName())
                .userId(ingredient.getRecipe().getUser().getId())
                .build();

    }
}
