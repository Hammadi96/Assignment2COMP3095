package ca.gb.comp3095.foodrecipe.view;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/recipe/search")
@Slf4j
public class SearchViewController {

    @GetMapping("/search")
    public String searchRecipe(Model model) {
        return "recipe/search-recipe";
    }
}
