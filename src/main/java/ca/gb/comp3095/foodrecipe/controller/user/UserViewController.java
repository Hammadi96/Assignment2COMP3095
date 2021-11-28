package ca.gb.comp3095.foodrecipe.controller.user;

import ca.gb.comp3095.foodrecipe.model.domain.Recipe;
import ca.gb.comp3095.foodrecipe.model.domain.User;
import ca.gb.comp3095.foodrecipe.model.service.RecipeService;
import ca.gb.comp3095.foodrecipe.model.service.UserService;
import ca.gb.comp3095.foodrecipe.view.AttributeTags;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.security.Principal;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

@Controller
@RequestMapping(value = "/user", produces = {MediaType.APPLICATION_JSON_VALUE})
@Slf4j
public class UserViewController implements WebMvcConfigurer {
    @Autowired
    UserService userService;

    @Qualifier("inMemoryUserDetailsManager")
    @Autowired
    UserDetailsManager userDetailsManager;

    @Autowired
    RecipeService recipeService;

    @GetMapping
    public String getUserDetails(Model model) {
        log.info("default user details");
        return "user/user";
    }


    @GetMapping(path = "/")
    @PreAuthorize("hasRole('USER')")
    public String getUserByName(@RequestParam("name") String userName, Model model) {
        try {
            UserDto userFound = userService.getUserByName(userName).map(UserConverter::fromDomain).orElseThrow(RuntimeException::new);
            model.addAttribute("user", userFound);
        } catch (Exception e) {
            log.warn("No user found for {}", userName, e);
            model.addAttribute("message", "User not found for name: " + userName);
        }
        return "user/user";
    }

    @GetMapping(path = "/details")
    @PreAuthorize("hasRole('USER')")
    public String showDetails(Principal principal, Model model) {
        try {
            Optional<User> userByName = userService.getUserByName(principal.getName().toLowerCase(Locale.ROOT));
            UserDto userFound = userByName.map(UserConverter::fromDomain).orElseThrow(RuntimeException::new);
            List<Recipe> allRecipesForUser = recipeService.getAllRecipesForUser(userFound.getId());
            model.addAttribute(AttributeTags.USER, userFound);
            model.addAttribute("recipeCounts", allRecipesForUser.size());
        } catch (Exception e) {
            log.warn("No user found for id {}", principal.getName(), e);
            model.addAttribute(AttributeTags.ERROR, "User not found for user: " + principal.getName());
        }
        return "user/user";
    }

    @GetMapping(path = "/id/{userId}")
    @PreAuthorize("hasRole('USER')")
    public String getUserById(@PathVariable("userId") Long userId, Model model) {
        try {
            UserDto userFound = userService.getUserById(userId).map(UserConverter::fromDomain).orElseThrow(RuntimeException::new);
            model.addAttribute("user", userFound);
        } catch (Exception e) {
            log.warn("No user found for id {}", userId, e);
            model.addAttribute("message", "User not found for id: " + userId);
        }
        return "user/user";
    }

    @GetMapping(path = "/create")
    public String showUserCreationForm(CreateUserCommand createUserCommand) {
        return "user/user-add";
    }

    @PostMapping(path = "/{userId}/change-password")
    public String changeUserPassword(@PathVariable("userId") Long userId, @Validated ChangeUserPasswordCommand changePasswordCommand, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            log.warn("Unable to change password, form has errors {}", bindingResult.getAllErrors());
            return "redirect:/user/id/" + userId;
        }

        if (!StringUtils.equals(changePasswordCommand.getPassword1(), changePasswordCommand.getPassword2())) {
            model.addAttribute(AttributeTags.ERROR, "Passwords do not match!");
            return "redirect:/user/id/" + userId;
        }

        Optional<User> userById = userService.getUserById(userId);
        if (userById.isEmpty()) {
            log.warn("No user found for id {}", userId);
            model.addAttribute(AttributeTags.ERROR, "Invalid user provided!");
            return "redirect:/";
        }

        log.info("user exists {} by name ", userDetailsManager.userExists(userById.get().getName()));

        try {
            User updatedUser = userService.changePassword(userId, changePasswordCommand.getPassword1());
            userDetailsManager.changePassword(userById.get().getPassword(), changePasswordCommand.getPassword1());
            model.addAttribute(AttributeTags.USER, UserConverter.fromDomain(updatedUser));
            model.addAttribute(AttributeTags.SUCCESS, "Password changed successfully!");
        } catch (Exception e) {
            log.warn("unable to change password for user {} ", userById, e);
            model.addAttribute(AttributeTags.USER, UserConverter.fromDomain(userById.get()));
            model.addAttribute(AttributeTags.ERROR, "Unable to change password for user " + userById.get().getName());
        }
        return "user/user";
    }

    @PostMapping(path = "/update")
    public String updateUserDetails(CreateUserCommand createUserCommand, BindingResult bindingResult, Model model, Principal principal) {
        if (bindingResult.hasErrors()) {
            log.warn("Unable to edit user details");
            return "redirect:/";
        }
        AtomicBoolean userNameChanged = new AtomicBoolean(false);
        AtomicBoolean emailChanged = new AtomicBoolean(false);
        try {
            Optional<User> userByName = userService.getUserByName(principal.getName().toLowerCase(Locale.ROOT));
            userByName.ifPresentOrElse(user -> {
                UserDetails userDetails = userDetailsManager.loadUserByUsername(user.getName());
                model.addAttribute(AttributeTags.USER, UserConverter.fromDomain(user));
                model.addAttribute("recipeCounts", recipeService.getAllRecipesForUser(user.getId()).size());
                if (!user.getName().equalsIgnoreCase(createUserCommand.getUserName()) && StringUtils.isNotEmpty(createUserCommand.getUserName())) {
                    if (isUserNameTaken(createUserCommand.getUserName())) {
                        log.warn("user name {} already taken !", createUserCommand.getUserName());
                        return;
                    }
                    user.setName(createUserCommand.getUserName().toLowerCase(Locale.ROOT));

                    userNameChanged.set(true);
                }
                if (StringUtils.isNotEmpty(createUserCommand.getEmail()) && !StringUtils.equalsIgnoreCase(user.getEmail(), createUserCommand.getEmail())) {
                    user.setEmail(createUserCommand.getEmail());
                    emailChanged.set(true);
                }

                if (userNameChanged.get() || emailChanged.get()) {
                    final User savedUser = userService.saveUser(user);
                    log.info("user details updated successfully");
                    if (userNameChanged.get()) {
                        UserDetails updatedUserDetails = org.springframework.security.core.userdetails.User.withUserDetails(userDetails)
                                .username(createUserCommand.getUserName())
                                .build();
                        userDetailsManager.createUser(updatedUserDetails); // create a new copy of the user name
                        return;
                    }
                    model.addAttribute(AttributeTags.USER, UserConverter.fromDomain(savedUser));
                    model.addAttribute(AttributeTags.SUCCESS, "User details changed successfully!");
                }
            }, RuntimeException::new);
        } catch (RuntimeException e) {
            log.warn("user not found for name {}", principal.getName(), e);
            model.addAttribute(AttributeTags.WARNING, "Unable to update user details!");
        }
        if (userNameChanged.get()) {
            try {
                userDetailsManager.deleteUser(principal.getName().toLowerCase(Locale.ROOT));
            } catch (Exception e) {
                log.warn("Unable to delete user {}, will simply logout", principal.getName(), e);
            }
            return "redirect:/logout";
        }
        return "user/user";

    }

    @PostMapping(path = "/create")
    public String createNewUser(@Validated CreateUserCommand createUserCommand, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "user/signup";
        }

        if (isUserNameTaken(createUserCommand.getUserName())) {
            log.warn("user name {} already taken, cannot create user", createUserCommand.getUserName());
            model.addAttribute(AttributeTags.ERROR, "Choose a different user name please!");
            return "user/signup";
        }
        try {
            User user = User.builder().name(createUserCommand.getUserName()).email(createUserCommand.getEmail()).password(createUserCommand.getPassword()).build();
            UserDto userDto = UserConverter.fromDomain(userService.createNewUser(user));
            log.info("successfully created user {}", userDto);
            userDetailsManager.createUser(
                    org.springframework.security.core.userdetails.User
                            .withDefaultPasswordEncoder()
                            .username(userDto.getUserName())
                            .password(userDto.getPassword())
                            .roles("USER")
                            .build());
            model.addAttribute("message", String.format("User %s created successfully", userDto.getUserName()));
            model.addAttribute("user", userDto);
        } catch (Exception e) {
            log.error("Unable to create user {}", createUserCommand, e);
        }
        return "redirect:/login";
    }

    private boolean isUserNameTaken(final String userName) {
        try {
            userDetailsManager.loadUserByUsername(userName);
            return true;
        } catch (UsernameNotFoundException e) {
            log.info("Username {} not found", userName);
            return false;
        }
    }

    @GetMapping(path = "/signup")
    public String signUp() {
        return "user/signup";
    }

}
