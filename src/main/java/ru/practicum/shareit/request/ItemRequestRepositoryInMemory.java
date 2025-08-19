package ru.practicum.shareit.request;

import java.util.HashMap;
import java.util.Optional;

public class ItemRequestRepositoryInMemory implements ItemRequestRepository {
    private final HashMap<Long, ItemRequest> requests = new HashMap<>();

    @Override
    public Optional<ItemRequest> getRequestById(Long id) {
        return Optional.ofNullable(requests.get(id));
    }
}
