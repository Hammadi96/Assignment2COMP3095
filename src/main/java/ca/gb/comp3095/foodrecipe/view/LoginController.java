package ca.gb.comp3095.foodrecipe.view;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String login(Model model) {
        return "login";
    }


    @GetMapping("/logout")
    public String logout(Model model) {
        model.addAttribute("user", "no user");
        return "home";
    }

}
