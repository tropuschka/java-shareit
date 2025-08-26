package ru.practicum.shareit.item;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.request.ItemRequest;

import java.util.HashMap;

@Data
@RequiredArgsConstructor
public class Item {
    private Long id;
    private String name;
    private String description;
    private Long owner;
    private boolean available;
    private ItemRequest request;
    private HashMap<Long, String> feedback = new HashMap<>();
}
