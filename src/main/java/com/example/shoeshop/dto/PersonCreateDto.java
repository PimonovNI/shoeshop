package com.example.shoeshop.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PersonCreateDto {

    @NotEmpty(message = "Введіть логін")
    @Size(min = 2, max = 128, message = "Має бути біль 2 символів і меньше 128")
    private String username;

    @NotEmpty(message = "Введіть пошту")
    @Email(message = "Неправильний формат пошти")
    private String email;

    @NotEmpty(message = "Введіть пароль")
    @Size(min = 2, max = 128, message = "Має бути біль 2 символів і меньше 128")
    private String password;

}
