package ca.gb.comp3095.foodrecipe.controller.recipe;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
public class SearchRecipeCommand {
    String title;
    String description;
    String ingredients;
    String tags;
    long cookingTimeUnder;
    long servings;
}
