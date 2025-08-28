package ru.practicum.shareit.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class User {
    private Long id;
    @NotBlank(message = "Имя пользователя не должно быть пустым")
    private String name;
    @NotBlank(message = "Почта не должна быть пустой")
    @Email(message = "Указана некорректная почта")
    private String email;
}
