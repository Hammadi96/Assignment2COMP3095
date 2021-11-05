package ca.gb.comp3095.foodrecipe.view;

import ca.gb.comp3095.foodrecipe.controller.recipe.CreateRecipeCommand;
import ca.gb.comp3095.foodrecipe.controller.recipe.RecipeConverter;
import ca.gb.comp3095.foodrecipe.controller.recipe.RecipeDto;
import ca.gb.comp3095.foodrecipe.model.domain.Recipe;
import ca.gb.comp3095.foodrecipe.model.domain.User;
import ca.gb.comp3095.foodrecipe.model.service.RecipeService;
import ca.gb.comp3095.foodrecipe.model.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.time.Duration;
import java.util.Optional;

import static ca.gb.comp3095.foodrecipe.view.AttributeTags.ERROR;
import static ca.gb.comp3095.foodrecipe.view.AttributeTags.RECIPE;
import static ca.gb.comp3095.foodrecipe.view.AttributeTags.SUCCESS;
import static ca.gb.comp3095.foodrecipe.view.AttributeTags.WARNING;

@Controller
@RequestMapping("/view/recipe")
@Slf4j
public class RecipeViewController {
    @Autowired
    RecipeService recipeService;

    @Autowired
    UserService userService;

    @PostMapping("/edit/{id}")
    public String editRecipe(@PathVariable Long id, @Validated RecipeDto recipeDto, BindingResult bindingResult, Model model, Principal principal) {
        log.debug("editing recipe {}", id);
        if (bindingResult.hasErrors()) {
            log.warn("form has errors, please fix them before editing");
            return "redirect:/view/recipe/" + id;
        }
        Optional<Recipe> recipe = recipeService.getRecipeById(id);
        if (recipe.isEmpty()) {
            recipeNotFoundMessage(id, model);
            return "redirect:/";
        }
        Optional<User> userByName = userService.getUserByName(principal.getName());
        if (userByName.isEmpty()) {
            log.warn("no user found for {}, stopping operation", principal.getName());
            return "redirect:/";
        }

        if (!recipe.get().getUser().getName().equals(principal.getName())) {
            log.warn("User {} cannot edit recipe, as it's owned by {}", principal.getName(), recipe.get().getUser().getName());
            model.addAttribute(WARNING, "You are not allowed to edit the recipe " + id);
            return "redirect:/";
        }

        try {
            Recipe newRecipe = RecipeConverter.toDomain(recipeDto);
            newRecipe.setUser(recipe.get().getUser());
            newRecipe.setId(id);
            RecipeDto updatedRecipe = RecipeConverter.toDto(recipeService.updateRecipe(newRecipe));
            log.info("updated recipe {}", updatedRecipe);
            model.addAttribute(RECIPE, String.valueOf(updatedRecipe));
            model.addAttribute(SUCCESS, "Recipe " + id + " changed successfully!");
            return viewRecipe(id, model);
        } catch (Exception e) {
            log.warn("Unable to update recipe {}", recipeDto, e);
            model.addAttribute(ERROR, "Unable to update recipe!");
            recipeNotFoundMessage(id, model);
            return "redirect:/";
        }
    }

    private void recipeNotFoundMessage(@PathVariable Long id, Model model) {
        model.addAttribute(ERROR, "No recipe found with id " + id);
    }

    @GetMapping("/{id}")
    public String viewRecipe(@PathVariable Long id, Model model) {
        Optional<Recipe> recipe = recipeService.getRecipeById(id);
        recipe.ifPresent(r -> model.addAttribute(RECIPE, RecipeConverter.toDto(r)));
        if (recipe.isEmpty()) {
            recipeNotFoundMessage(id, model);
        }
        return "recipe/recipe";
    }

    @GetMapping("/create")
    public String createNewRecipe(CreateRecipeCommand createRecipeCommand) {
        return "recipe/new-recipe";
    }

    @GetMapping("/edit/{id}")
    public String editRecipe(@PathVariable Long id, Model model) {
        Optional<Recipe> recipe = recipeService.getRecipeById(id);
        recipe.ifPresent(r -> {
            log.info("recipe found for id {}", id);
            model.addAttribute(RECIPE, RecipeConverter.toDto(r));
        });
        if (recipe.isEmpty()) {
            recipeNotFoundMessage(id, model);
        }
        return "recipe/edit-recipe";
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('USER')")
    public String submitNewRecipe(@Validated CreateRecipeCommand createRecipeCommand, BindingResult bindingResult, Model model, Principal principal) {
        if (bindingResult.hasErrors()) {
            return "recipe/new-recipe";
        }
        Optional<User> userByName = userService.getUserByName(principal.getName());
        if (userByName.isEmpty()) {
            log.warn("no user found for {}, stopping operation", principal.getName());
            return "redirect:/";
        }

        try {
            log.info("creating recipe {}", createRecipeCommand);
            Recipe recipe = Recipe.builder().user(User.builder().id(userByName.get().getId()).build())
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
            model.addAttribute(RECIPE, recipeDto);
            model.addAttribute(SUCCESS, "Recipe submitted successfully!");
            return "redirect:/view/recipe/" + recipeDto.getId();
        } catch (Exception e) {
            log.warn("Unable to create recipe command {}", createRecipeCommand, e);
            model.addAttribute(ERROR, "Unable to create recipe!");
            return "recipe/recipe";
        }
    }

    @GetMapping("/search")
    public String searchRecipe(Model model) {
        return "recipe/search-recipe";
    }
}
