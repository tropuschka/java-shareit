package user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import practicum.ru.shareit.ShareItServer;
import practicum.ru.shareit.exceptions.DuplicationException;
import practicum.ru.shareit.exceptions.NotFoundException;
import practicum.ru.shareit.user.User;
import practicum.ru.shareit.user.UserRepository;
import practicum.ru.shareit.user.UserServiceImpl;
import practicum.ru.shareit.user.dto.UserDto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@ContextConfiguration(classes = ShareItServer.class)
@Import({UserServiceImpl.class})
@ActiveProfiles("test")
class UserServiceImplTest {
    @Autowired
    private UserServiceImpl userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TestEntityManager entityManager;
    private User user1;
    private User user2;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        entityManager.flush();
        entityManager.clear();

        user1 = new User();
        user1.setName("Llew");
        user1.setEmail("llew@mail.com");
        user1 = entityManager.persistAndFlush(user1);
        user2 = new User();
        user2.setName("Almaz");
        user2.setEmail("almaz@mail.com");
        user2 = entityManager.persistAndFlush(user2);
    }

    @Test
    void createUser() {
        UserDto userDto = new UserDto(null, "Fabiana", "fabiana_diaz@mail.com");
        UserDto createdUserDto = userService.createUser(userDto);

        assertThat(createdUserDto.getId()).isNotNull();
        assertThat(createdUserDto.getName()).isEqualTo("Fabiana");
        assertThat(createdUserDto.getEmail()).isEqualTo("fabiana_diaz@mail.com");

        User savedUser = entityManager.find(User.class, createdUserDto.getId());
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getName()).isEqualTo("Fabiana");
        assertThat(savedUser.getEmail()).isEqualTo("fabiana_diaz@mail.com");
    }

    @Test
    void createUserWithExistingEmail() {
        UserDto userDto = new UserDto(null, "Delmar", "llew@mail.com");
        final DuplicationException exception = assertThrows(DuplicationException.class,
                () -> userService.createUser(userDto));
        assertEquals("Пользователь с такой почтой уже существует", exception.getMessage());
    }

    @Test
    void updateUser() {
        UserDto userDto = new UserDto(null, "Lisette", "lisette_trump@mail.com");
        UserDto updatedUserDto = userService.updateUser(user1.getId(), userDto);

        assertThat(updatedUserDto.getId()).isNotNull();
        assertThat(updatedUserDto.getName()).isEqualTo("Lisette");
        assertThat(updatedUserDto.getEmail()).isEqualTo("lisette_trump@mail.com");

        User updatedUser = entityManager.find(User.class, user1.getId());
        assertThat(updatedUser).isNotNull();
        assertThat(updatedUser.getName()).isEqualTo("Lisette");
        assertThat(updatedUser.getEmail()).isEqualTo("lisette_trump@mail.com");
    }

    @Test
    void updateUserName() {
        UserDto userDto = new UserDto(null, "Jeremiah", null);
        UserDto updatedUserDto = userService.updateUser(user1.getId(), userDto);

        assertThat(updatedUserDto.getId()).isNotNull();
        assertThat(updatedUserDto.getName()).isEqualTo("Jeremiah");
        assertThat(updatedUserDto.getEmail()).isEqualTo("llew@mail.com");

        User updatedUser = entityManager.find(User.class, user1.getId());
        assertThat(updatedUser).isNotNull();
        assertThat(updatedUser.getName()).isEqualTo("Jeremiah");
        assertThat(updatedUser.getEmail()).isEqualTo("llew@mail.com");
    }

    @Test
    void updateUserEmail() {
        UserDto userDto = new UserDto(null, null, "friduric_ahmad@mail.com");
        UserDto updatedUserDto = userService.updateUser(user1.getId(), userDto);

        assertThat(updatedUserDto.getId()).isNotNull();
        assertThat(updatedUserDto.getName()).isEqualTo("Llew");
        assertThat(updatedUserDto.getEmail()).isEqualTo("friduric_ahmad@mail.com");

        User updatedUser = entityManager.find(User.class, user1.getId());
        assertThat(updatedUser).isNotNull();
        assertThat(updatedUser.getName()).isEqualTo("Llew");
        assertThat(updatedUser.getEmail()).isEqualTo("friduric_ahmad@mail.com");
    }

    @Test
    void updateUserWithSameEmail() {
        UserDto userDto = new UserDto(null, null, "llew@mail.com");
        UserDto updatedUserDto = userService.updateUser(user1.getId(), userDto);

        assertThat(updatedUserDto.getId()).isNotNull();
        assertThat(updatedUserDto.getName()).isEqualTo("Llew");
        assertThat(updatedUserDto.getEmail()).isEqualTo("llew@mail.com");

        User updatedUser = entityManager.find(User.class, user1.getId());
        assertThat(updatedUser).isNotNull();
        assertThat(updatedUser.getName()).isEqualTo("Llew");
        assertThat(updatedUser.getEmail()).isEqualTo("llew@mail.com");
    }

    @Test
    void updateUserWithExistingEmail() {
        UserDto userDto = new UserDto(null, null, "llew@mail.com");
        final DuplicationException exception = assertThrows(DuplicationException.class,
                () -> userService.updateUser(user2.getId(), userDto));
        assertEquals("Пользователь с такой почтой уже существует", exception.getMessage());
    }

    @Test
    void updateNotExistingUser() {
        userRepository.delete(user1);
        UserDto userDto = new UserDto(null, "Josephine", null);
        final NotFoundException exception = assertThrows(NotFoundException.class,
                () -> userService.updateUser(user1.getId(), userDto));
        assertEquals("Пользователь с ID " + user1.getId() + " не найден", exception.getMessage());
    }

    @Test
    void findUserDtoById() {
        UserDto foundUserDto = userService.findUserDtoById(user1.getId());
        assertThat(foundUserDto).isNotNull();
        assertThat(foundUserDto.getName()).isEqualTo("Llew");
        assertThat(foundUserDto.getEmail()).isEqualTo("llew@mail.com");
    }

    @Test
    void findNotExistingUserById() {
        userRepository.delete(user1);
        final NotFoundException exception = assertThrows(NotFoundException.class,
                () -> userService.findUserDtoById(user1.getId()));
        assertEquals("Пользователь с ID " + user1.getId() + " не найден", exception.getMessage());
    }

    @Test
    void deleteUser() {
        User notDeletedUser = entityManager.find(User.class, user1.getId());
        assertThat(notDeletedUser).isNotNull();
        userService.deleteUser(user1.getId());
        User deletedUser = entityManager.find(User.class, user1.getId());
        assertThat(deletedUser).isNull();
    }

    @Test
    void deleteNotExistingUser() {
        userRepository.delete(user1);
        final NotFoundException exception = assertThrows(NotFoundException.class,
                () -> userService.deleteUser(user1.getId()));
        assertEquals("Пользователь с ID " + user1.getId() + " не найден", exception.getMessage());
    }
}