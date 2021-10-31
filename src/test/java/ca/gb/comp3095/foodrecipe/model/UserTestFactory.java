package ca.gb.comp3095.foodrecipe.model;

import ca.gb.comp3095.foodrecipe.model.domain.User;

import java.util.Random;

public class UserTestFactory {

    public static User aRandomUser(String name) {
        return User.builder().id(new Random().nextLong()).email("test@test.com").name(name).build();
    }
}
