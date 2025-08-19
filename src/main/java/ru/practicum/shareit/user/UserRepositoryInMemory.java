package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Optional;

@Repository
public class UserRepositoryInMemory implements UserRepository {
    HashMap<Long, User> user = new HashMap<>();

    @Override
    public Optional<User> getUserById(Long id) {
        return Optional.ofNullable(user.get(id));
    }
}
