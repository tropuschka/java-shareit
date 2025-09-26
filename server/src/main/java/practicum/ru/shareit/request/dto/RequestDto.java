package practicum.ru.shareit.request.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import practicum.ru.shareit.item.dto.ItemDto;
import practicum.ru.shareit.user.dto.UserDto;
import practicum.ru.shareit.validation.Marker;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
public class RequestDto {
    private Long id;
    @NotBlank(groups = Marker.OnCreate.class, message = "Описание запроса не должно быть пустым")
    private String description;
    private UserDto requestor;
    private LocalDateTime created;
    private List<ItemDto> items = new ArrayList<>();
}
