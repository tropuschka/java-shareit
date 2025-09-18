package practicum.ru.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import practicum.ru.shareit.validation.Marker;

import java.util.ArrayList;
import java.util.List;

@Data
@RequiredArgsConstructor
public class ItemDto {
    private Long id;
    @NotBlank(groups = Marker.OnCreate.class, message = "Название предмета не должно быть пустым")
    private String name;
    @NotBlank(groups = Marker.OnCreate.class, message = "Описание предмета не должно быть пустым")
    private String description;
    @NotNull(groups = Marker.OnCreate.class, message = "Статус должен быть указан")
    private Boolean available;
    private List<CommentDto> comments = new ArrayList<>();
    private Long request;
}
