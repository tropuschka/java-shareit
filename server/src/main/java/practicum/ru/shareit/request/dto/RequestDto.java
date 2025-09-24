package practicum.ru.shareit.request.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import practicum.ru.shareit.user.User;
import practicum.ru.shareit.validation.Marker;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class RequestDto {
    private Long id;
    @NotBlank(groups = Marker.OnCreate.class, message = "Описание запроса не должно быть пустым")
    private String description;
    private User requestor;
    private LocalDateTime created;
}
