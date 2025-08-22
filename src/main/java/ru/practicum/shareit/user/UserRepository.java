package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;
import java.util.Optional;

public interface UserRepository {
    Optional<User> getUserById(Long id);

    Collection<User> getAll();

    User addUser(User user);
}
