package ru.practicum.shareit.item;

import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Optional;

@Repository
public class ItemRepositoryInMemory implements ItemRepository {
    private HashMap<Long, Item> items = new HashMap<>();

    @Override
    public void createItem(Item item) {
        item.setId(nextId());
        items.put(item.getId(), item);
    }

    @Override
    public Optional<Item> getItemById(Long itemId) {
        return Optional.ofNullable(items.get(itemId));
    }

    @Override
    public void updateItem(Long itemId, Item item) {
        items.put(itemId, item);
    }

    private Long nextId() {
        Long maxId = items.keySet().stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0L);
        maxId++;
        return maxId;
    }
}
