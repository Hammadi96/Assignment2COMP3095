package ca.gb.comp3095.foodrecipe.controller.user;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder(toBuilder = true)
public class UserDto {
    Long id;
    String userName;
    String password;
    Instant creationTime;
}
