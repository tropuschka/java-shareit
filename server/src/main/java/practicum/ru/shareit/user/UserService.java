package practicum.ru.shareit.user;

import practicum.ru.shareit.user.dto.UserDto;

public interface UserService {
    UserDto createUser(UserDto userDto);

    UserDto updateUser(Long userId, UserDto userDto);

    UserDto findUserDtoById(Long userId);

    void deleteUser(Long userId);
}
