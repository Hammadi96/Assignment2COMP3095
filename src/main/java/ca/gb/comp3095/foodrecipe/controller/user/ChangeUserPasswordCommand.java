package ca.gb.comp3095.foodrecipe.controller.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@AllArgsConstructor
@Data
@Builder
public class ChangeUserPasswordCommand {
    @NotNull @NotEmpty
    String password1;
    @NotNull @NotEmpty
    String password2;
}
