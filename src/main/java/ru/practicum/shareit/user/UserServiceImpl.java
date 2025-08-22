package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.ConditionsNotMetException;
import ru.practicum.shareit.exceptions.DuplicationException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Objects;

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
        validateEmail(userDto);
        User user = userDtoToUser(userDto);
        return userRepository.addUser(user);
    }

    @Override
    public User updateUser(Long userId, UserDto userDto) {
        User user = userRepository.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + userId + " не найден"));
        if (userDto.getName() != null && !userDto.getName().isBlank()) {
            user.setName(userDto.getName());
        }
        if (userDto.getEmail() != null && !userDto.getEmail().isBlank()) {
            validateEmail(userDto, userId);
            user.setEmail(userDto.getEmail());
        }
        userRepository.updateUser(userId, user);
        return user;
    }

    private void checkUserDto(UserDto userDto) {
        if (userDto.getName() == null || userDto.getName().isBlank()) {
            throw new ConditionsNotMetException("Имя пользователя не должно быть пустым");
        }
        if (userDto.getEmail() == null || userDto.getEmail().isBlank()) {
            throw new ConditionsNotMetException("Почта не должна быть пустой");
        }
    }

    private void validateEmail(UserDto userDto, Long userId) {
        boolean duplicatedEmail = userRepository.getAll().stream()
                .anyMatch(u -> u.getEmail().equals(userDto.getEmail()) && u.getId() != userId);
        if (duplicatedEmail) throw new DuplicationException("Пользователь с такой почтой уже существует");
        if (!userDto.getEmail().contains("@")) throw new ConditionsNotMetException("Почта должна содержать символ @");
    }

    private void validateEmail(UserDto userDto) {
        boolean duplicatedEmail = userRepository.getAll().stream()
                .anyMatch(u -> u.getEmail().equals(userDto.getEmail()));
        if (duplicatedEmail) throw new DuplicationException("Пользователь с такой почтой уже существует");
        if (!userDto.getEmail().contains("@")) throw new ConditionsNotMetException("Почта должна содержать символ @");
    }

    private User userDtoToUser(UserDto userDto) {
        User user = new User();
        if (userDto.getName() != null && !userDto.getName().isBlank()) user.setName(userDto.getName());
        if (userDto.getEmail() != null && !userDto.getEmail().isBlank()) user.setEmail(userDto.getEmail());
        return user;
    }
}
