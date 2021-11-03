package ca.gb.comp3095.foodrecipe.controller.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateUserCommand {
    @NotNull
    String userName;
    @NotNull
    String email;
    @NotNull
    String password;
}
