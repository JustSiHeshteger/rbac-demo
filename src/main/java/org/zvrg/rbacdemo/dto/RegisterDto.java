package org.zvrg.rbacdemo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RegisterDto {

    @NotNull
    @Size(min = 5, max = 100)
    private String login;
    @NotNull
    @Size(min = 5, max = 100)
    private String password;
    @NotNull
    @Email
    private String email;

}
