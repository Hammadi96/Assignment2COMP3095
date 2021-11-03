package ca.gb.comp3095.foodrecipe.model.service;

import ca.gb.comp3095.foodrecipe.controller.recipe.SearchRecipeCommand;
import ca.gb.comp3095.foodrecipe.model.domain.Recipe;

import java.util.List;

public interface SearchService {
    List<Recipe> findAllBy(SearchRecipeCommand searchRecipeCommand);
}
