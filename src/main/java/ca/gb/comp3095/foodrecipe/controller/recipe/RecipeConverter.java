package ca.gb.comp3095.foodrecipe.controller.recipe;

import ca.gb.comp3095.foodrecipe.model.domain.Ingredient;
import ca.gb.comp3095.foodrecipe.model.domain.Recipe;
import ca.gb.comp3095.foodrecipe.model.domain.User;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.Collection;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

@Slf4j
public class RecipeConverter {
    private static final BiFunction<Recipe, String, RecipeDto> recipeMapperWithLikes = (recipe, userName) -> {
        RecipeDto recipeDto = RecipeConverter.toDto(recipe);
        if (isLikedByUser(recipe, userName)) {
            log.info("recipe {} is liked by user {}", recipe, userName);
            recipeDto.setIsLikedByCurrentUser(true);
        }
        return recipeDto;
    };

    private static boolean isLikedByUser(final Recipe recipe, final String name) {
        if (recipe.getLikedBy() == null || recipe.getLikedBy().isEmpty()) {
            return false;
        }
        return recipe.getLikedBy().stream().map(User::getName).anyMatch(n -> n.equalsIgnoreCase(name));
    }

    public static Recipe toDomain(final RecipeDto recipeDto) {
        Recipe recipe = Recipe.builder()
                .id(recipeDto.getId())
                .title(recipeDto.getTitle())
                .description(recipeDto.getDescription())
                .cookingTime(Duration.parse(String.format("PT%dM", recipeDto.getCookingTime())))
                .servings(recipeDto.getServings())
                .instructions(recipeDto.getCookingInstructions())
                .ingredients(recipeDto.getIngredients())
                .user(User.builder().id(recipeDto.getUserId()).build()).build();
        Optional.ofNullable(recipeDto.getAllIngredients()).ifPresent(ingredients -> recipe.setAllIngredients(ingredients.stream().map(ing -> Ingredient.builder().name(ing).recipe(recipe).build()).collect(Collectors.toSet())));
        return recipe;
    }

    public static RecipeDto toDto(final Recipe recipe) {
        RecipeDto recipeDto = RecipeDto.builder()
                .creationTime(recipe.getCreationTime())
                .lastModified(recipe.getModificationTime())
                .id(recipe.getId()).title(recipe.getTitle())
                .description(recipe.getDescription())
                .cookingTime(recipe.getCookingTime().toMinutes())
                .servings(recipe.getServings())
                .cookingInstructions(recipe.getInstructions())
                .ingredients(recipe.getIngredients())
                .submittedBy(recipe.getUser().getName())
                .userId(recipe.getUser().getId()).build();

        Optional.ofNullable(recipe.getAllIngredients())
                .ifPresent(ingredients -> recipeDto.setAllIngredients(ingredients.stream().map(Ingredient::getName).collect(Collectors.toSet())));
        return recipeDto;
    }

    public static RecipeDto toDtoWithLikedBy(final Recipe recipe, final String currentUserName) {
        return recipeMapperWithLikes.apply(recipe, currentUserName);
    }
}
