package ru.practicum.shareit.item.dto;

import lombok.Data;

@Data
public class ItemDto {
    private String name;
    private String description;
    private Boolean available;
    private Long request;

    public ItemDto(String name, String description, Boolean available, Long requestId) {
        this.name = name;
        this.description = description;
        this.available = available;
        this.request = requestId;
    }
}
