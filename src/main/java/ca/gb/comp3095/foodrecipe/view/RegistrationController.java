package ca.gb.comp3095.foodrecipe.view;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/register")
public class RegistrationController {

    @GetMapping
    public String home(Model model) {
        return "register";
    }


    @PostMapping
    public String registerNewUser(Model model) {
        return "home";
    }
}
