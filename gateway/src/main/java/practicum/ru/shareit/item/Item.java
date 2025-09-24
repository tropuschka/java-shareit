package practicum.ru.shareit.item;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class Item {
    private Long id;
    private String name;
    private String description;
    private Long owner;
    private Boolean available;
    private Long request;
}
