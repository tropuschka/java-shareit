package ru.practicum.shareit.item;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.request.ItemRequest;

import java.util.HashMap;

@Data
@RequiredArgsConstructor
public class Item {
    private Long id;
    @NotBlank(message = "Название предмета не должно быть пустым")
    private String name;
    @NotBlank(message = "Описание предмета не должно быть пустым")
    private String description;
    private Long owner;
    @NotBlank(message = "Статус должен быть указан")
    private Boolean available;
    private ItemRequest request;
    private HashMap<Long, String> feedback = new HashMap<>();
}
