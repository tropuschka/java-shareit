package ru.practicum.shareit.user;

import java.util.Optional;

public interface UserRepository {
    Optional<User> getUserById(Long id);
}
