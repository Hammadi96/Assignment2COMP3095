package ca.gb.comp3095.foodrecipe.controller.recipe;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
public class IngredientDto {
    Instant creationTime;
    Instant lastModified;
    Long id;
    String name;
    Long recipeId;
    Long userId;
}
