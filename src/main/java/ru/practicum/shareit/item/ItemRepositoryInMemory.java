package ru.practicum.shareit.item;

import org.springframework.stereotype.Repository;

import java.util.HashMap;

@Repository
public class ItemRepositoryInMemory implements ItemRepository {
    private HashMap<Long, Item> items = new HashMap<>();
}
