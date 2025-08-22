package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;
import java.util.HashMap;
import java.util.Optional;

@Repository
public class UserRepositoryInMemory implements UserRepository {
    HashMap<Long, User> users = new HashMap<>();

    @Override
    public Optional<User> getUserById(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public Collection<User> getAll() {
        return users.values();
    }

    @Override
    public User addUser(User user) {
        user.setId(nextId());
        users.put(user.getId(), user);
        return user;
    }

    private Long nextId() {
        Long maxId = users.keySet().stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0L);
        maxId++;
        return maxId;
    }
}
