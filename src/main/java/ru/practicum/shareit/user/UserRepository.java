package ru.practicum.shareit.user;

import java.util.Collection;
import java.util.Optional;

public interface UserRepository {
    Optional<User> getUserById(Long id);

    Collection<User> getAll();

    User addUser(User user);

    void updateUser(Long userId, User user);

    void deleteUser(Long userId);
}
