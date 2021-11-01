package ca.gb.comp3095.foodrecipe.controller.recipe;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
public class CreateRecipeCommand {
    String title;
    String description;
    List<String> ingredientsList;
    List<String> cookingInstructionList;
    String ingredients;
    String cookingInstructions;
    Long servings;
    String imageUrl;
    Long cookingTime;
    Long userId;
}
