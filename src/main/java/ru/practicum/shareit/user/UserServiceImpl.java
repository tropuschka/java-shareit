package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.ConditionsNotMetException;
import ru.practicum.shareit.exceptions.DuplicationException;
import ru.practicum.shareit.user.dto.UserDto;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User createUser(UserDto userDto) {
        checkUserDto(userDto);
        User user = new User();
        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());
        return userRepository.addUser(user);
    }

    private void checkUserDto(UserDto userDto) {
        if (userDto.getName() == null || userDto.getName().isBlank()) {
            throw new ConditionsNotMetException("Имя пользователя не должно быть пустым");
        }
        if (userDto.getEmail() == null || userDto.getEmail().isBlank()) {
            throw new ConditionsNotMetException("Почта не должна быть пустой");
        }
        boolean duplicatedEmail = userRepository.getAll().stream()
                .anyMatch(u -> u.getEmail().equals(userDto.getEmail()));
        if (duplicatedEmail) throw new DuplicationException("Пользователь с такой почтой уже существует");
        if (!userDto.getEmail().contains("@")) throw new ConditionsNotMetException("Почта должна содержать символ @");
    }
}
