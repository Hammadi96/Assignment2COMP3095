package ca.gb.comp3095.foodrecipe.view;

import ca.gb.comp3095.foodrecipe.controller.recipe.CreateRecipeCommand;
import ca.gb.comp3095.foodrecipe.controller.recipe.RecipeConverter;
import ca.gb.comp3095.foodrecipe.controller.recipe.RecipeDto;
import ca.gb.comp3095.foodrecipe.model.domain.Recipe;
import ca.gb.comp3095.foodrecipe.model.domain.User;
import ca.gb.comp3095.foodrecipe.model.service.RecipeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.Duration;
import java.util.Optional;

@Controller
@RequestMapping("/view/recipe")
@Slf4j
public class RecipeViewController {
    @Autowired
    RecipeService recipeService;

    @GetMapping("/{id}")
    public String viewRecipe(@PathVariable Long id, Model model) {
        Optional<Recipe> recipe = recipeService.getRecipeById(id);
        recipe.ifPresent(r -> model.addAttribute("recipe", RecipeConverter.toDto(r)));
        return "recipe/recipe";
    }

    @GetMapping("/create")
    public String createNewRecipe(CreateRecipeCommand createRecipeCommand) {
        return "recipe/new-recipe";
    }

    @PostMapping("/create")
    public String submitNewRecipe(@Validated CreateRecipeCommand createRecipeCommand, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "recipe/new-recipe";
        }
        try {
            log.info("creating recipe {}", createRecipeCommand);
            Recipe recipe = Recipe.builder().user(User.builder().id(createRecipeCommand.getUserId()).build())
                    .title(createRecipeCommand.getTitle())
                    .description(createRecipeCommand.getDescription())
                    .cookingTime(Duration.parse(String.format("PT%dM", createRecipeCommand.getCookingTime())))
                    .servings(createRecipeCommand.getServings())
                    .imageUrl(createRecipeCommand.getImageUrl())
                    .ingredients(createRecipeCommand.getIngredients())
                    .instructions(createRecipeCommand.getCookingInstructions())
                    .build();

            RecipeDto recipeDto = RecipeConverter.toDto(recipeService.createRecipe(recipe));
            log.info("recipe created {}", recipeDto);
            model.addAttribute("recipe", recipeDto);
            return "redirect:/view/recipe/" + recipeDto.getId();
        } catch (Exception e) {
            log.warn("Unable to create recipe command {}", createRecipeCommand, e);
            model.addAttribute("message", "Unable to create recipe!");
            return "recipe/recipe";
        }
    }

    @GetMapping("/edit-recipe/{id}")
    public String editRecipe(@PathVariable Long id, Model model) {
        log.debug("editing recipe {}", id);
        model.addAttribute("recipeId", id);
        return "recipe/edit-recipe";
    }
}
