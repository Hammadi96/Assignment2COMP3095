package ca.gb.comp3095.foodrecipe.view;

import ca.gb.comp3095.foodrecipe.controller.recipe.RecipeConverter;
import ca.gb.comp3095.foodrecipe.controller.recipe.RecipeDto;
import ca.gb.comp3095.foodrecipe.controller.recipe.SearchRecipeCommand;
import ca.gb.comp3095.foodrecipe.model.domain.Recipe;
import ca.gb.comp3095.foodrecipe.model.service.SearchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.stream.Collectors;

import static ca.gb.comp3095.foodrecipe.view.AttributeTags.ERROR;
import static ca.gb.comp3095.foodrecipe.view.AttributeTags.MESSAGE;
import static ca.gb.comp3095.foodrecipe.view.AttributeTags.RECIPES;
import static ca.gb.comp3095.foodrecipe.view.AttributeTags.SUCCESS;
import static ca.gb.comp3095.foodrecipe.view.AttributeTags.WARNING;

@Controller
@RequestMapping("/recipe/search")
@Slf4j
public class SearchViewController {
    @Autowired
    SearchService searchService;

    @GetMapping()
    public String getSearch(SearchRecipeCommand searchRecipeCommand) {
        return "recipe/search-recipe";
    }

    @PostMapping()
    public String searchByCommand(SearchRecipeCommand searchRecipeCommand, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            log.warn("binding errors {}", bindingResult.getAllErrors().stream().map(String::valueOf).collect(Collectors.joining(",")));
            return "recipe/search-recipe";
        }
        try {
            List<Recipe> recipesFound = searchService.findAllBy(searchRecipeCommand);
            List<RecipeDto> recipeDtos = recipesFound.stream().map(RecipeConverter::toDto).collect(Collectors.toList());
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
}
