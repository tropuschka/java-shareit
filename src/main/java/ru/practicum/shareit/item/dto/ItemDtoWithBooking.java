package ru.practicum.shareit.item.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class ItemDtoWithBooking {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private Long lastBooking;
    private Long nextBooking;
}
