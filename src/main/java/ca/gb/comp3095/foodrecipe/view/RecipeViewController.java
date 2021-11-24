package ca.gb.comp3095.foodrecipe.view;

import ca.gb.comp3095.foodrecipe.controller.recipe.CreateRecipeCommand;
import ca.gb.comp3095.foodrecipe.controller.recipe.IngredientConverter;
import ca.gb.comp3095.foodrecipe.controller.recipe.RecipeConverter;
import ca.gb.comp3095.foodrecipe.controller.recipe.RecipeDto;
import ca.gb.comp3095.foodrecipe.model.domain.Recipe;
import ca.gb.comp3095.foodrecipe.model.domain.User;
import ca.gb.comp3095.foodrecipe.model.service.RecipeService;
import ca.gb.comp3095.foodrecipe.model.service.ShoppingCartService;
import ca.gb.comp3095.foodrecipe.model.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.HashSet;
import java.util.Optional;
import java.util.stream.Collectors;

import static ca.gb.comp3095.foodrecipe.view.AttributeTags.ERROR;
import static ca.gb.comp3095.foodrecipe.view.AttributeTags.INGREDIENTS;
import static ca.gb.comp3095.foodrecipe.view.AttributeTags.RECIPE;
import static ca.gb.comp3095.foodrecipe.view.AttributeTags.SUCCESS;
import static ca.gb.comp3095.foodrecipe.view.AttributeTags.TOTAL_ITEMS;
import static ca.gb.comp3095.foodrecipe.view.AttributeTags.WARNING;

@Controller
@RequestMapping("/view/recipe")
@Slf4j
public class RecipeViewController {
    @Autowired
    RecipeService recipeService;

    @Autowired
    UserService userService;

    @Autowired
    ShoppingCartService shoppingCartService;

    @PostMapping("/edit/{id}")
    public String editRecipe(@PathVariable Long id, @Validated RecipeDto recipeDto, BindingResult bindingResult, Model model, Principal principal) { //TODO fix the ingredients editing here
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
            model.addAttribute(TOTAL_ITEMS, shoppingCartService.getTotal().longValue());
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
        recipe.ifPresent(r -> {
            model.addAttribute(RECIPE, RecipeConverter.toDto(r));
            model.addAttribute(INGREDIENTS, r.getAllIngredients().stream().map(IngredientConverter::toDto).collect(Collectors.toList()));
        });
        if (recipe.isEmpty()) {
            recipeNotFoundMessage(id, model);
        }
        model.addAttribute(TOTAL_ITEMS, shoppingCartService.getTotal().longValue());
        return "recipe/recipe";
    }

    @GetMapping("/create")
    public String createNewRecipe(CreateRecipeCommand createRecipeCommand, final Model model) {
        model.addAttribute(TOTAL_ITEMS, shoppingCartService.getTotal().longValue());
        return "recipe/new-recipe";
    }

    @GetMapping("/edit/{id}")
    public String editRecipe(@PathVariable Long id, Model model) {
        Optional<Recipe> recipe = recipeService.getRecipeById(id);
        recipe.ifPresent(r -> {
            log.info("recipe found for id {}", id);
            model.addAttribute(RECIPE, RecipeConverter.toDto(r));
            model.addAttribute(INGREDIENTS, r.getAllIngredients().stream().map(IngredientConverter::toDto).collect(Collectors.toList()));
        });
        if (recipe.isEmpty()) {
            recipeNotFoundMessage(id, model);
        }
        model.addAttribute(TOTAL_ITEMS, shoppingCartService.getTotal().longValue());
        return "recipe/edit-recipe";
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('USER')")
    public String submitNewRecipe(@Validated CreateRecipeCommand createRecipeCommand, BindingResult bindingResult, Model model, Principal principal) {
        model.addAttribute(TOTAL_ITEMS, shoppingCartService.getTotal().longValue());
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
            RecipeDto recipeDto = RecipeDto.builder()
                    .title(createRecipeCommand.getTitle())
                    .description(createRecipeCommand.getDescription())
                    .cookingTime(createRecipeCommand.getCookingTime())
                    .servings(createRecipeCommand.getServings())
                    .imageUrl(createRecipeCommand.getImageUrl())
                    .cookingInstructions(createRecipeCommand.getCookingInstructions())
                    .build();
            if (!CollectionUtils.isEmpty(createRecipeCommand.getIngredientsList())) {
                recipeDto.setAllIngredients(new HashSet<>(createRecipeCommand.getIngredientsList()));
            } else {
                recipeDto.setAllIngredients(new HashSet<>());
            }
            Recipe recipe = RecipeConverter.toDomain(recipeDto);
            recipe.setUser(userByName.get());

            RecipeDto savedRecipeDto = RecipeConverter.toDto(recipeService.createRecipe(recipe));
            log.info("recipe created {}", savedRecipeDto);
            model.addAttribute(RECIPE, savedRecipeDto);
            model.addAttribute(SUCCESS, "Recipe submitted successfully!");
            return "redirect:/view/recipe/" + savedRecipeDto.getId();
        } catch (Exception e) {
            log.warn("Unable to create recipe command {}", createRecipeCommand, e);
            model.addAttribute(ERROR, "Unable to create recipe!");
            return "recipe/recipe";
        }
    }

    @GetMapping("/search")
    public String searchRecipe(Model model) {
        model.addAttribute(TOTAL_ITEMS, shoppingCartService.getTotal().longValue());
        return "recipe/search-recipe";
    }
}
