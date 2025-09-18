package practicum.ru.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import practicum.ru.shareit.validation.Marker;

@Data
@AllArgsConstructor
public class UserDto {
    private Long id;
    @NotBlank(groups = Marker.OnCreate.class, message = "Имя пользователя не должно быть пустым")
    private String name;
    @NotBlank(groups = Marker.OnCreate.class, message = "Почта не должна быть пустой")
    @Email(message = "Указана некорректная почта")
    private String email;
}
