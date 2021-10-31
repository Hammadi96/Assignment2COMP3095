package ca.gb.comp3095.foodrecipe.model;

import ca.gb.comp3095.foodrecipe.model.domain.User;

public class UserTestFactory {

    public static User aRandomUser(String name) {
        return User.builder().id(1L).email("test@test.com").name(name).build();
    }
}
