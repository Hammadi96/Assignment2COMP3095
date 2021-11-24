package ca.gb.comp3095.foodrecipe.view;

import ca.gb.comp3095.foodrecipe.controller.recipe.RecipeConverter;
import ca.gb.comp3095.foodrecipe.controller.recipe.RecipeDto;
import ca.gb.comp3095.foodrecipe.controller.recipe.SearchRecipeCommand;
import ca.gb.comp3095.foodrecipe.model.domain.Recipe;
import ca.gb.comp3095.foodrecipe.model.domain.User;
import ca.gb.comp3095.foodrecipe.model.repo.RecipeRespository;
import ca.gb.comp3095.foodrecipe.model.repo.UserRepository;
import ca.gb.comp3095.foodrecipe.model.service.SearchService;
import ca.gb.comp3095.foodrecipe.model.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import static ca.gb.comp3095.foodrecipe.view.AttributeTags.ERROR;
import static ca.gb.comp3095.foodrecipe.view.AttributeTags.RECIPES;
import static ca.gb.comp3095.foodrecipe.view.AttributeTags.SUCCESS;
import static ca.gb.comp3095.foodrecipe.view.AttributeTags.TOTAL_ITEMS;
import static ca.gb.comp3095.foodrecipe.view.AttributeTags.WARNING;

@Controller
@RequestMapping("/recipe/search")
@Slf4j
public class SearchViewController {
    @Autowired
    SearchService searchService;

    @Autowired
    ShoppingCartService shoppingCartService;

    @GetMapping()
    public String getSearch(SearchRecipeCommand searchRecipeCommand, final Model model) {
        model.addAttribute(TOTAL_ITEMS, shoppingCartService.getTotal().longValue());
        return "recipe/search-recipe";
    }

    @GetMapping("/all")
    public String getAllRecipes(Model model, Principal principal) {
        try {
            List<RecipeDto> recipes = searchService.findAll().stream()
                    .map(recipe -> recipeMapperWithLikes.apply(recipe, principal))
                    .collect(Collectors.toList());
            model.addAttribute(RECIPES, recipes);
            log.info("current items in cart {}", shoppingCartService.getItemsInCart());
            log.info("total items in cart {}", shoppingCartService.getTotal());
            model.addAttribute(TOTAL_ITEMS, shoppingCartService.getTotal().longValue());
        } catch (Exception e) {
            log.warn("Unable to get all recipes", e);
            model.addAttribute(ERROR, "Unable to get recipes at the moment!");
        }
        return "recipe/search-results";
    }

    @PostMapping()
    public String searchByCommand(SearchRecipeCommand searchRecipeCommand, BindingResult bindingResult, Model model, Principal principal) {
        model.addAttribute(TOTAL_ITEMS, shoppingCartService.getTotal().longValue());
        if (bindingResult.hasErrors()) {
            log.warn("binding errors {}", bindingResult.getAllErrors().stream().map(String::valueOf).collect(Collectors.joining(",")));
            return "recipe/search-recipe";
        }
        try {
            List<Recipe> recipesFound = searchService.findAllBy(searchRecipeCommand);
            List<RecipeDto> recipeDtos = recipesFound.stream().map(recipe -> recipeMapperWithLikes.apply(recipe, principal)).collect(Collectors.toList());
            if (recipeDtos.isEmpty()) {
                model.addAttribute(WARNING, "No recipes found for your search!");
                return "recipe/search-recipe";
            } else {
                model.addAttribute(RECIPES, recipeDtos);
                model.addAttribute(SUCCESS, recipesFound.size() + " recipe(s) found!");
            }
        } catch (Exception e) {
            log.warn("Unable to search for recipes {}", searchRecipeCommand, e);
            model.addAttribute(ERROR, "unable to search recipes at the moment");
            return "recipe/search-recipe";
        }
        return "recipe/search-results";
    }

    private static boolean isLikedByUser(final Recipe recipe, final String name) {
        if (recipe.getLikedBy() == null || recipe.getLikedBy().isEmpty()) {
            return false;
        }
        return recipe.getLikedBy().stream().map(User::getName).anyMatch(n -> n.equalsIgnoreCase(name));
    }

    private final BiFunction<Recipe, Principal, RecipeDto> recipeMapperWithLikes = (recipe, principal) -> {
        RecipeDto recipeDto = RecipeConverter.toDto(recipe);
        if (isLikedByUser(recipe, principal.getName())) {
            log.info("recipe {} is liked by user {}", recipe, principal.getName());
            recipeDto.setIsLikedByCurrentUser(true);
        }
        return recipeDto;
    };

}
