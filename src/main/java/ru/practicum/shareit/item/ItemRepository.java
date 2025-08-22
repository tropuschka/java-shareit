package ru.practicum.shareit.item;

import java.util.Collection;
import java.util.Optional;

public interface ItemRepository {
    void createItem(Item item);

    Optional<Item> getItemById(Long itemId);

    void updateItem(Long itemId, Item item);

    Collection<Item> getUserItems(Long userId);
}
