package ca.gb.comp3095.foodrecipe.controller.user;

import ca.gb.comp3095.foodrecipe.model.domain.User;

public class UserConverter {
    public static UserDto fromDomain(User user) {
        return UserDto.builder().id(user.getId()).userName(user.getName()).email(user.getEmail()).password(user.getPassword()).creationTime(user.getCreationTime()).build();
    }

    public static User fromDto(UserDto userDto) {
        return User.builder().id(userDto.getId()).name(userDto.getUserName()).email(userDto.getEmail()).password(userDto.getPassword()).build();
    }

}
