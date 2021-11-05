package ca.gb.comp3095.foodrecipe.controller.recipe;

import ca.gb.comp3095.foodrecipe.model.domain.Recipe;
import ca.gb.comp3095.foodrecipe.model.domain.User;
import ca.gb.comp3095.foodrecipe.model.service.RecipeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

@RestController
@RequestMapping(value = "/v1/recipe", produces = {MediaType.APPLICATION_JSON_VALUE})
@Slf4j
public class RecipeController {

    @Autowired
    RecipeService recipeService;

    @GetMapping("/{recipeId}")
    public ResponseEntity<RecipeDto> createRecipeForUser(@PathVariable("recipeId") Long recipeId) {
        try {
            return recipeService.getRecipeById(recipeId)
                    .map(recipe -> new ResponseEntity<>(RecipeConverter.toDto(recipe), HttpStatus.FOUND))
                    .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            log.error("Unable to get recipe for id {}", recipeId, e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(path = "/create/user/{userId}", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<RecipeDto> createRecipeForUser(@PathVariable("userId") Long userId, @RequestBody CreateRecipeCommand createRecipeCommand) {
        try {
            Recipe recipe = Recipe.builder().user(User.builder().id(userId).build())
                    .title(createRecipeCommand.getTitle())
                    .description(createRecipeCommand.getDescription())
                    .cookingTime(Duration.parse(String.format("PT%dM", createRecipeCommand.getCookingTime())))
                    .servings(createRecipeCommand.getServings())
                    .imageUrl(createRecipeCommand.getImageUrl())
                    .ingredients(createRecipeCommand.getIngredients())
                    .instructions(createRecipeCommand.getCookingInstructions())
                    .build();
            RecipeDto recipeDto = RecipeConverter.toDto(recipeService.createRecipe(recipe));
            return new ResponseEntity<>(recipeDto, HttpStatus.CREATED);
        } catch (Exception e) {
            log.error("Unable to create recipe for user {}, command {}", userId, createRecipeCommand, e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}