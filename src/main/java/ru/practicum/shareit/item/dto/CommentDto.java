package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.validation.Marker;

import java.time.LocalDateTime;

@Data
@RequiredArgsConstructor
public class CommentDto {
    private Long id;
    @NotBlank(groups = Marker.OnCreate.class, message = "Текст комментария не должен быть пустым")
    private String text;
    private Long itemId;
    private Long authorId;
    private String authorName;
    private LocalDateTime created;
}
