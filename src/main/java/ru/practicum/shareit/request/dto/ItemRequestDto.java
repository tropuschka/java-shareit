package ru.practicum.shareit.request.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.validation.Marker;

@Data
@AllArgsConstructor
public class ItemRequestDto {
    private Long id;
    @NotBlank(groups = Marker.OnCreate.class, message = "Описание запроса не должно быть пустым")
    private String description;
    @NotNull(groups = Marker.OnCreate.class, message = "ID пользователя, делающего запрос, должен быть указан")
    private Long requestor;
}
