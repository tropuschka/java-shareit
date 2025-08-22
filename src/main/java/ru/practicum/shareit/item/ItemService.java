package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;

public interface ItemService {
    Item addItem(Long userId, ItemDto itemDto);

    Item updateItem(Long userId, Long itemId, ItemDto itemDto);

    Item getItemById(Long itemId);

    Collection<Item> getUserItems(Long userId);

    Collection<Item> searchItem(String query);
}
