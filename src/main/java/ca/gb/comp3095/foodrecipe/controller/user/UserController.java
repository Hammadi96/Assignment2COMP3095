package ca.gb.comp3095.foodrecipe.controller.user;

import ca.gb.comp3095.foodrecipe.model.domain.User;
import ca.gb.comp3095.foodrecipe.model.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/user", produces = {MediaType.APPLICATION_JSON_VALUE})
@Slf4j
public class UserController {
    @Autowired
    UserService userService;

    @GetMapping(path = "/")
    public ResponseEntity<UserDto> getUserByName(@RequestParam("name") String userName) {
        try {
            UserDto userFound = userService.getUserByName(userName).map(UserConverter::fromDomain).orElseThrow(RuntimeException::new);
            return new ResponseEntity<>(userFound, HttpStatus.OK);
        } catch (Exception e) {
            log.error("No user found for {}", userName, e);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(path = "/id/{userId}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long userId) {
        try {
            UserDto userFound = userService.getUserById(userId).map(UserConverter::fromDomain).orElseThrow(RuntimeException::new);
            return new ResponseEntity<>(userFound, HttpStatus.OK);
        } catch (Exception e) {
            log.error("No user found for id {}", userId, e);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping(path = "/create", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<UserDto> createNewUser(@RequestBody CreateUserCommand createUserCommand) {
        try {
            User user = User.builder().name(createUserCommand.getUserName()).password(createUserCommand.getPassword()).build();
            UserDto userDto = UserConverter.fromDomain(userService.createNewUser(user));
            return new ResponseEntity<>(userDto, HttpStatus.CREATED);
        } catch (Exception e) {
            log.error("Unable to create user {}", createUserCommand, e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
