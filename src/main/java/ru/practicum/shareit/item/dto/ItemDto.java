package ru.practicum.shareit.item.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
public class ItemDto {
    private String name;
    private String description;
    private boolean available;
    private Long request;

    public ItemDto(String name, String description, boolean available, Long requestId) {
        this.name = name;
        this.description = description;
        this.available = available;
        this.request = requestId;
    }
}
