package user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import practicum.ru.shareit.user.User;
import practicum.ru.shareit.user.UserRepository;
import practicum.ru.shareit.user.UserServiceImpl;
import practicum.ru.shareit.user.dto.UserDto;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(UserServiceImpl.class)
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
        user2 = new User();
        user2.setName("Almaz");
        user2.setEmail("almaz@mail.com");
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
    void updateUser() {
    }

    @Test
    void findUserDtoById() {
    }

    @Test
    void deleteUser() {
    }
}