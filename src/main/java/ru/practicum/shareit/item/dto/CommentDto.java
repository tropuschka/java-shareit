package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.validation.Marker;

@Data
@RequiredArgsConstructor
public class CommentDto {
    private Long id;
    @NotBlank(groups = Marker.OnCreate.class, message = "Текст комментария не должен быть пустым")
    private String text;
    @NotNull(groups = Marker.OnCreate.class, message = "ID предмета должен быть указан")
    private Long itemId;
    private Long authorId;
}
