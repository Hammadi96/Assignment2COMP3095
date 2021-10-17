package ca.gb.comp3095.foodrecipe.controller.recipe;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class CreateRecipeCommand {
    String title;
    String description;
    List<String> ingredients;
    List<String> cookingInstructions;
    Long userId;
}
