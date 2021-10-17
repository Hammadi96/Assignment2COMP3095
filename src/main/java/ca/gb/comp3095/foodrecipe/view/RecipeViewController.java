package ca.gb.comp3095.foodrecipe.view;

import ca.gb.comp3095.foodrecipe.controller.recipe.RecipeConverter;
import ca.gb.comp3095.foodrecipe.model.domain.Recipe;
import ca.gb.comp3095.foodrecipe.model.service.RecipeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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
    public String createNewRecipe(Model model) {
        return "recipe/new-recipe";
    }

    @PostMapping("/create")
    public String submitNewRecipe(Model model) {
        return "recipe/recipe";
    }

    @GetMapping("/edit-recipe/{id}")
    public String editRecipe(@PathVariable Long id, Model model) {
        log.debug("editing recipe {}", id);
        model.addAttribute("recipeId", id);
        return "recipe/edit-recipe";
    }
}
