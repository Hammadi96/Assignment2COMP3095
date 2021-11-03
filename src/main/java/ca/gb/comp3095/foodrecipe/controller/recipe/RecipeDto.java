package ca.gb.comp3095.foodrecipe.controller.recipe;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.Instant;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
public class RecipeDto implements Serializable {
    Instant creationTime;
    Instant lastModified;
    @NotNull
    Long id;
    @NotEmpty
    String title;
    @NotEmpty
    String description;
    String imageUrl;
    @NotNull
    Long cookingTime;
    @NotNull
    Long servings;
    @NotNull @NotEmpty
    String cookingInstructions;
    @NotNull @NotEmpty
    String ingredients;
    @NotNull
    Long userId;
}
