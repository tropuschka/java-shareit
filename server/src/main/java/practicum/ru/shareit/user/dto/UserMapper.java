package practicum.ru.shareit.user.dto;

import practicum.ru.shareit.user.User;

public class UserMapper {
    public static UserDto toUserDto(User user) {
        return new UserDto(user.getId(), user.getName(), user.getEmail());
    }

    public static User toUser(UserDto userDto) {
        User user = new User();
        if (userDto.getEmail() != null && !userDto.getEmail().isBlank()) user.setEmail(userDto.getEmail());
        if (userDto.getName() != null && !userDto.getName().isBlank()) user.setName(userDto.getName());
        return user;
    }
}
