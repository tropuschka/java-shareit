package practicum.ru.shareit.item.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@RequiredArgsConstructor
public class ItemDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private List<CommentDto> comments = new ArrayList<>();
    private Long requestId;
}
