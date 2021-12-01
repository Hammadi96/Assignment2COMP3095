package ca.gb.comp3095.foodrecipe.controller.user;

import ca.gb.comp3095.foodrecipe.controller.recipe.RecipeConverter;
import ca.gb.comp3095.foodrecipe.controller.recipe.RecipeDto;
import ca.gb.comp3095.foodrecipe.model.domain.Recipe;
import ca.gb.comp3095.foodrecipe.model.domain.User;
import ca.gb.comp3095.foodrecipe.model.service.RecipeService;
import ca.gb.comp3095.foodrecipe.model.service.ShoppingCartService;
import ca.gb.comp3095.foodrecipe.model.service.UserService;
import ca.gb.comp3095.foodrecipe.view.AttributeTags;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.security.Principal;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static ca.gb.comp3095.foodrecipe.view.AttributeTags.MESSAGE;
import static ca.gb.comp3095.foodrecipe.view.AttributeTags.TOTAL_ITEMS;
import static ca.gb.comp3095.foodrecipe.view.AttributeTags.WARNING;

@Controller
@RequestMapping(value = "/user/recipe", produces = {MediaType.APPLICATION_JSON_VALUE})
@Slf4j
public class UserRecipeViewController implements WebMvcConfigurer {
    @Autowired
    UserService userService;

    @Autowired
    RecipeService recipeService;

    @Autowired
    ShoppingCartService shoppingCartService;

    @GetMapping(path = "/all")
    @PreAuthorize("hasRole('USER')")
    public String getAllUserRecipes(Principal principal, Model model) {
        try {
            Optional<User> userByName = userService.getUserByName(principal.getName());
            UserDto userFound = userByName.map(UserConverter::fromDomain).orElseThrow(RuntimeException::new);
            List<RecipeDto> allRecipesForUser = recipeService.getAllRecipesForUser(userFound.getId()).stream().map(recipe -> RecipeConverter.toDtoWithLikedBy(recipe, principal.getName())).collect(Collectors.toList());
            model.addAttribute(AttributeTags.USER, userFound);
            model.addAttribute(TOTAL_ITEMS, shoppingCartService.getTotal().longValue());
            if (allRecipesForUser.isEmpty()) {
                model.addAttribute(WARNING, "Oops, you haven't added any recipes yet!");
            } else {
                model.addAttribute(MESSAGE, allRecipesForUser.size() + " recipes found!");
                model.addAttribute(AttributeTags.RECIPES, allRecipesForUser);
            }
        } catch (Exception e) {
            log.warn("No user found for id {}", principal.getName(), e);
            model.addAttribute(AttributeTags.ERROR, "User not found for user: " + principal.getName());
        }
        return "recipe/search-results";
    }

    @GetMapping(path = "/favorites")
    @PreAuthorize("hasRole('USER')")
    public String getFavoriteRecipes(Principal principal, Model model) {
        try {
            Optional<User> userByName = userService.getUserByName(principal.getName());
            User userFound = userByName.orElseThrow(RuntimeException::new);
            List<RecipeDto> allLikedRecipesByUser = userFound.getLikedRecipes().stream().map(recipe -> RecipeConverter.toDtoWithLikedBy(recipe, principal.getName())).collect(Collectors.toList());
            model.addAttribute(AttributeTags.USER, userFound);
            if (allLikedRecipesByUser.isEmpty()) {
                model.addAttribute(WARNING, "Oops, you haven't liked any recipes yet!");
            } else {
                model.addAttribute(MESSAGE, allLikedRecipesByUser.size() + " recipes found!");
                model.addAttribute(AttributeTags.RECIPES, allLikedRecipesByUser);
            }
            model.addAttribute(TOTAL_ITEMS, shoppingCartService.getTotal().longValue());
        } catch (Exception e) {
            log.warn("No user found for id {}", principal.getName(), e);
            model.addAttribute(AttributeTags.ERROR, "User not found for user: " + principal.getName());
        }
        return "recipe/search-results";
    }

    @GetMapping(path = "{recipe-id}/favorite")
    public ResponseEntity toggleFavoriteRecipe(@PathVariable(name = "recipe-id") Long recipeId, Principal principal, Model model) {
        log.info("marking recipe {} as favorite for user {}", recipeId, principal.getName());
        try {
            Optional<User> userByName = userService.getUserByName(principal.getName());
            User userFound = userByName.orElseThrow(RuntimeException::new);
            Optional<Recipe> recipeById = recipeService.getRecipeById(recipeId);
            if (recipeById.isEmpty()) {
                log.warn("No recipe found for id {}, will skip marking favorite/unfavorite", recipeById);
                return new ResponseEntity(HttpStatus.BAD_REQUEST);
            }
            recipeById.ifPresent(recipe -> {
                if (userFound.getLikedRecipes() == null) {
                    userFound.setLikedRecipes(new HashSet<>());
                }
                if (recipe.getLikedBy() == null) {
                    recipe.setLikedBy(new HashSet<>());
                }
                Set<Recipe> likedRecipes = userFound.getLikedRecipes();
                if (likedRecipes.contains(recipe)) {
                    log.info("removing already liked recipe {} for user {}", recipe.getId(), userFound.getName());
                    likedRecipes.remove(recipe);
                    recipe.getLikedBy().remove(userFound);
                } else {
                    log.info("liking recipe {} for user {}", recipe.getId(), userFound.getName());
                    likedRecipes.add(recipe);
                    recipe.getLikedBy().add(userFound);
                }
                userService.saveUser(userFound);
                recipeService.updateRecipe(recipe);
            });

        } catch (RuntimeException e) {
            log.warn("Unable to mark recipe {} as favorite/unfavorite ", recipeId, e);
            new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity(HttpStatus.OK);

    }
}
