package ru.practicum.shareit.item;

import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

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

    @Override
    public Collection<Item> getUserItems(Long userId) {
        return items.values().stream()
                .filter(i -> Objects.equals(i.getOwner(), userId))
                .collect(Collectors.toSet());
    }

    @Override
    public Collection<Item> searchItem(String query) {
        return items.values().stream()
                .filter(i -> (i.getName().toUpperCase().contains(query.toUpperCase())
                        || (i.getDescription() != null &&
                        i.getDescription().toUpperCase().contains(query.toUpperCase()))) && i.getAvailable())
                .collect(Collectors.toSet());
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
