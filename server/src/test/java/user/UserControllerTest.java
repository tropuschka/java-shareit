package user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import practicum.ru.shareit.exceptions.ConditionsNotMetException;
import practicum.ru.shareit.user.UserController;
import practicum.ru.shareit.user.UserService;
import practicum.ru.shareit.user.dto.UserDto;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {
    @Mock
    private UserService userService;
    @InjectMocks
    private UserController userController;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private MockMvc mockMvc;
    private UserDto userDto;
    private UserDto returnUserDto;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
        userDto = new UserDto(null, "Lavinia", "lavinia@mail.com");
        returnUserDto = new UserDto(1L, "Lavinia", "lavinia@mail.com");
    }

    @Test
    public void createUser() throws Exception {
        when(userService.createUser(any(UserDto.class))).thenReturn(returnUserDto);

        mockMvc.perform(post("/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
        verify(userService, times(1)).createUser(userDto);
    }

    @Test
    public void createUserWithInvalidEmail() throws Exception {
        userDto = new UserDto(null, "Asma'u", "asma_u");
        when(userService.createUser(any(UserDto.class)))
                .thenThrow(new ConditionsNotMetException("Указана некорректная почта"));

        final ConditionsNotMetException exception = Assertions.assertThrows(ConditionsNotMetException.class,
                () -> userService.createUser(userDto));
        Assertions.assertEquals("Указана некорректная почта", exception.getMessage());
    }

    @Test
    public void createUserWithoutEmail() throws Exception {
        userDto = new UserDto(null, "Samira", "");
        when(userService.createUser(any(UserDto.class)))
                .thenThrow(new ConditionsNotMetException("Почта не должна быть пустой"));

        final ConditionsNotMetException exception = Assertions.assertThrows(ConditionsNotMetException.class,
                () -> userService.createUser(userDto));
        Assertions.assertEquals("Почта не должна быть пустой", exception.getMessage());
    }

    @Test
    public void createUserWithoutName() throws Exception {
        userDto = new UserDto(null, "", "bertil_beringer@mail.com");
        when(userService.createUser(any(UserDto.class)))
                .thenThrow(new ConditionsNotMetException("Имя пользователя не должно быть пустым"));

        final ConditionsNotMetException exception = Assertions.assertThrows(ConditionsNotMetException.class,
                () -> userService.createUser(userDto));
        Assertions.assertEquals("Имя пользователя не должно быть пустым", exception.getMessage());
    }

    @Test
    public void updateUser() throws Exception {
        when(userService.updateUser(anyLong(), any())).thenReturn(returnUserDto);

        mockMvc.perform(patch("/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Lavinia"));
        verify(userService, times(1)).updateUser(1L, userDto);
    }

    @Test
    public void updateUserWithInvalidEmail() throws Exception {
        userDto = new UserDto(null, "Iveta", "ivetamail");
        when(userService.updateUser(anyLong(), any(UserDto.class)))
                .thenThrow(new ConditionsNotMetException("Указана некорректная почта"));

        final ConditionsNotMetException exception = Assertions.assertThrows(ConditionsNotMetException.class,
                () -> userService.updateUser(1L, userDto));
        Assertions.assertEquals("Указана некорректная почта", exception.getMessage());
    }

    @Test
    public void getUserById() throws Exception {
        when(userService.findUserDtoById(anyLong())).thenReturn(returnUserDto);

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("lavinia@mail.com"));
        verify(userService, times(1)).findUserDtoById(1L);
    }

    @Test
    public void deleteUser() throws Exception {
        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isOk());
        verify(userService, times(1)).deleteUser(1L);
    }
}
