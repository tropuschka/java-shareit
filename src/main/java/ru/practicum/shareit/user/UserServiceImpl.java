package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.DuplicationException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public UserDto createUser(UserDto userDto) {
        validateEmail(userDto);
        User user = UserMapper.toUser(userDto);
        return UserMapper.toUserDto(userRepository.addUser(user));
    }

    @Override
    public UserDto updateUser(Long userId, UserDto userDto) {
        User user = findUserById(userId);
        if (userDto.getName() != null && !userDto.getName().isBlank()) {
            user.setName(userDto.getName());
        }
        if (userDto.getEmail() != null && !userDto.getEmail().isBlank()) {
            validateEmail(userDto, userId);
            user.setEmail(userDto.getEmail());
        }
        userRepository.updateUser(userId, user);
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto findUserDtoById(Long userId) {
        return UserMapper.toUserDto(userRepository.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + userId + " не найден")));
    }

    @Override
    public void deleteUser(Long userId) {
        findUserById(userId);
        userRepository.deleteUser(userId);
    }

    private User findUserById(Long userId) {
        return userRepository.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + userId + " не найден"));
    }

    private void validateEmail(UserDto userDto, Long userId) {
        boolean duplicatedEmail = userRepository.getAll().stream()
                .anyMatch(u -> u.getEmail().equals(userDto.getEmail()) && !Objects.equals(u.getId(), userId));
        if (duplicatedEmail) throw new DuplicationException("Пользователь с такой почтой уже существует");
    }

    private void validateEmail(UserDto userDto) {
        boolean duplicatedEmail = userRepository.getAll().stream()
                .anyMatch(u -> u.getEmail().equals(userDto.getEmail()));
        if (duplicatedEmail) throw new DuplicationException("Пользователь с такой почтой уже существует");
    }
}
