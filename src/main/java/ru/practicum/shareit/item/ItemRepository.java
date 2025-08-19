package ru.practicum.shareit.item;

public interface ItemRepository {
    void createItem(Long userId, Item item);
}
