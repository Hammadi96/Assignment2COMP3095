package ca.gb.comp3095.foodrecipe.controller.recipe;

import lombok.Builder;
import lombok.Data;

import java.time.Duration;
import java.util.List;

@Builder
@Data
public class CreateRecipeCommand {
    String title;
    String description;
    List<String> ingredientsList;
    List<String> cookingInstructionList;
    String ingredients;
    String cookingInstructions;
    Long servings;
    String imageUrl;
    Duration cookingTime;
    Long userId;
}
