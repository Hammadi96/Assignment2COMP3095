package ca.gb.comp3095.foodrecipe.model.service;

import ca.gb.comp3095.foodrecipe.model.domain.Recipe;
import ca.gb.comp3095.foodrecipe.model.repo.RecipeRespository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class RecipeServiceImpl implements RecipeService {

    @Autowired
    RecipeRespository recipeRespository;

    @Override
    public Recipe createRecipe(Recipe recipe) {
        return recipeRespository.save(recipe);
    }

    @Override
    public Optional<Recipe> getRecipeById(Long id) {
        return recipeRespository.findById(id);
    }

    @Override
    public Recipe updateRecipe(Recipe recipe) {
        return recipeRespository.save(recipe);
    }

    @Override
    public List<Recipe> getAllRecipesForUser(Long userId) {
        return recipeRespository.findAllByUserId(userId);
    }

    @Override
    public boolean deleteRecipeById(Long id) {
        try {
            recipeRespository.deleteById(id);
            return true;
        } catch (Exception exception) {
            exception.printStackTrace();
            return false;
        }
    }
}
