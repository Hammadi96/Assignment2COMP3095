package ca.gb.comp3095.foodrecipe.model.service;

import ca.gb.comp3095.foodrecipe.controller.recipe.SearchRecipeCommand;
import ca.gb.comp3095.foodrecipe.model.domain.Recipe;
import ca.gb.comp3095.foodrecipe.model.repo.RecipeRespository;
import org.apache.commons.text.similarity.JaroWinklerSimilarity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
public class SearchServiceImpl implements SearchService {
    @Autowired
    RecipeRespository recipeRespository;

    JaroWinklerSimilarity jaroWinklerSimilarity = new JaroWinklerSimilarity();


    double threshold = 0.50;

    Predicate<Object> isNotNull = Predicate.not(Objects::isNull);
    BiPredicate<String, String> matchAboveThreshold = (left, right) -> isNotNull.test(left) && isNotNull.test(right) && jaroWinklerSimilarity.apply(left, right) > threshold;
    BiPredicate<Recipe, SearchRecipeCommand> titleSearch = ((recipe, searchRecipeCommand) -> matchAboveThreshold.test(recipe.getTitle(), searchRecipeCommand.getTitle()));
    BiPredicate<Recipe, SearchRecipeCommand> descriptionSearch = ((recipe, searchRecipeCommand) -> matchAboveThreshold.test(recipe.getDescription(), searchRecipeCommand.getDescription()));
    BiPredicate<Recipe, SearchRecipeCommand> ingredientSearch = ((recipe, searchRecipeCommand) -> matchAboveThreshold.test(recipe.getIngredients(), searchRecipeCommand.getIngredients()));
    BiPredicate<Recipe, SearchRecipeCommand> cookingTimeUnderSearch = ((recipe, searchRecipeCommand) -> isNotNull.test(recipe.getCookingTime()) && recipe.getCookingTime().toMinutes() < searchRecipeCommand.getCookingTimeUnder());

    BiPredicate<Recipe, SearchRecipeCommand> searchFilters = List.of(titleSearch, descriptionSearch, ingredientSearch, cookingTimeUnderSearch).stream().reduce(BiPredicate::or).orElse((r, s) -> false);

    @Override
    public List<Recipe> findAllBy(@NotNull final SearchRecipeCommand searchRecipeCommand) {
        return recipeRespository.findAll()
                .stream()
                .filter(recipe -> searchFilters.test(recipe, searchRecipeCommand))
                .collect(Collectors.toList());

    }
}
