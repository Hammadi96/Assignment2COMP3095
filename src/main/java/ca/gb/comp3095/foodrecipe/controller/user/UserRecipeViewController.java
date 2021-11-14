package ca.gb.comp3095.foodrecipe.controller.user;

import ca.gb.comp3095.foodrecipe.controller.recipe.RecipeConverter;
import ca.gb.comp3095.foodrecipe.controller.recipe.RecipeDto;
import ca.gb.comp3095.foodrecipe.model.domain.User;
import ca.gb.comp3095.foodrecipe.model.service.RecipeService;
import ca.gb.comp3095.foodrecipe.model.service.UserService;
import ca.gb.comp3095.foodrecipe.view.AttributeTags;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ca.gb.comp3095.foodrecipe.view.AttributeTags.MESSAGE;
import static ca.gb.comp3095.foodrecipe.view.AttributeTags.WARNING;

@Controller
@RequestMapping(value = "/user/recipe", produces = {MediaType.APPLICATION_JSON_VALUE})
@Slf4j
public class UserRecipeViewController implements WebMvcConfigurer {
    @Autowired
    UserService userService;
//
//    @Qualifier("inMemoryUserDetailsManager")
//    @Autowired
//    UserDetailsManager userDetailsManager;

    @Autowired
    RecipeService recipeService;

    @GetMapping(path = "/all")
    @PreAuthorize("hasRole('USER')")
    public String getAllUserRecipes(Principal principal, Model model) {
        try {
            Optional<User> userByName = userService.getUserByName(principal.getName());
            UserDto userFound = userByName.map(UserConverter::fromDomain).orElseThrow(RuntimeException::new);
            List<RecipeDto> allRecipesForUser = recipeService.getAllRecipesForUser(userFound.getId()).stream().map(RecipeConverter::toDto).collect(Collectors.toList());
            model.addAttribute(AttributeTags.USER, userFound);
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
        return "redirect:all";
    }
}
