package ca.gb.comp3095.foodrecipe.view;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Slf4j
public class WebController {


    @GetMapping(value = {"/home", "/"})
    public String greeting(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!(auth instanceof AnonymousAuthenticationToken)) {
            log.info("Active user session found with user name{}", auth.getName());
            return "redirect:/recipe/search";
        }
        return "redirect:/user/signup";
    }

    @GetMapping("/index")
    public String index(Model model) {
        model.addAttribute("user", "test user");
        return "redirect:/recipe/search";
    }
}
