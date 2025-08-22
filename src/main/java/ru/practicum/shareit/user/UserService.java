package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

public interface UserService {
    User createUser(UserDto userDto);

    User updateUser(Long userId, UserDto userDto);

    User findUserById(Long userId);

    void deleteUser(Long userId);
}
