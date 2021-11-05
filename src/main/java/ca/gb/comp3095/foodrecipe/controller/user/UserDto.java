package ca.gb.comp3095.foodrecipe.controller.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
public class UserDto {
    Long id;
    String userName;
    String email;
    String password;
    Instant creationTime;
}
