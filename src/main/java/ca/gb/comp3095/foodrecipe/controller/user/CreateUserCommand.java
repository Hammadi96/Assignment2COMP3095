package ca.gb.comp3095.foodrecipe.controller.user;

import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
