package ru.practicum.shareit.item;

import java.util.Optional;

public interface ItemRepository {
    void createItem(Item item);

    Optional<Item> getItemById(Long itemId);

    void updateItem(Long itemId, Item item);
}
