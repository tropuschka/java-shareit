package ru.practicum.shareit.request;

import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Optional;

@Repository
public class ItemRequestRepositoryInMemory implements ItemRequestRepository {
    private final HashMap<Long, ItemRequest> requests = new HashMap<>();

    @Override
    public Optional<ItemRequest> getRequestById(Long id) {
        return Optional.ofNullable(requests.get(id));
    }
}
