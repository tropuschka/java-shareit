package practicum.ru.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import practicum.ru.shareit.exceptions.DuplicationException;
import practicum.ru.shareit.exceptions.NotFoundException;
import practicum.ru.shareit.user.dto.UserDto;
import practicum.ru.shareit.user.dto.UserMapper;

import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public UserDto createUser(UserDto userDto) {
        validateEmail(userDto);
        User user = UserMapper.toUser(userDto);
        return UserMapper.toUserDto(userRepository.save(user));
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
        userRepository.save(user);
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto findUserDtoById(Long userId) {
        return UserMapper.toUserDto(userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + userId + " не найден")));
    }

    @Override
    public void deleteUser(Long userId) {
        User user = findUserById(userId);
        userRepository.delete(user);
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + userId + " не найден"));
    }

    private void validateEmail(UserDto userDto, Long userId) {
        Optional<User> duplicatedEmail = Optional.ofNullable(userRepository.findByEmail(userDto.getEmail()));
        if (duplicatedEmail.isPresent() && !Objects.equals(duplicatedEmail.get().getId(), userId)) {
            throw new DuplicationException("Пользователь с такой почтой уже существует");
        }
    }

    private void validateEmail(UserDto userDto) {
        Optional<User> duplicatedEmail = Optional.ofNullable(userRepository.findByEmail(userDto.getEmail()));
        if (duplicatedEmail.isPresent()) throw new DuplicationException("Пользователь с такой почтой уже существует");
    }
}
