package ru.practicum.shareit.item;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class Item {
    private Long id;
    @NotBlank(message = "Название предмета не должно быть пустым")
    private String name;
    @NotBlank(message = "Описание предмета не должно быть пустым")
    private String description;
    private Long owner;
    @NotNull(message = "Статус должен быть указан")
    private Boolean available;
}
