package ru.practicum.shareit.request.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.validation.Marker;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
public class RequestDto {
    private Long id;
    @NotBlank(groups = Marker.OnCreate.class, message = "Описание запроса не должно быть пустым")
    private String description;
    private User requestor;
    private LocalDateTime created;
    private List<Item> items = new ArrayList<>();
}
