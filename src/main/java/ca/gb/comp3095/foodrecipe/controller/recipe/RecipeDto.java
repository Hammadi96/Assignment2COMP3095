package ca.gb.comp3095.foodrecipe.controller.recipe;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder(toBuilder = true)
public class RecipeDto {
    Instant creationTime;
    Long id;
    String title;
    String description;
    Long userId;
}
