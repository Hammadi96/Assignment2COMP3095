package ca.gb.comp3095.foodrecipe.controller.recipe;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Builder
@Data
@AllArgsConstructor
public class SearchRecipeCommand {
    @NotNull
    String title;
    String description;
    String ingredients;
    String tags;
    Long cookingTimeUnder;
    Long servings;
}
