package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.validation.Marker;

@Data
@AllArgsConstructor
public class ItemDto {
    private Long id;
    @NotBlank(groups = Marker.OnCreate.class, message = "Название предмета не должно быть пустым")
    private String name;
    @NotBlank(groups = Marker.OnCreate.class, message = "Описание предмета не должно быть пустым")
    private String description;
    @NotNull(groups = Marker.OnCreate.class, message = "Статус должен быть указан")
    private Boolean available;
}
