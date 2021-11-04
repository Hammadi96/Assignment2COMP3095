package ca.gb.comp3095.foodrecipe.view;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {

    @GetMapping(value = {"/home", "/"})
    public String greeting(Model model) {
        model.addAttribute("user", "test user");
        return "redirect:/recipe/search";
    }

    @GetMapping("/index")
    public String index(Model model) {
        model.addAttribute("user", "test user");
        return "redirect:/recipe/search";
    }
}
