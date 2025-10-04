package request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import practicum.ru.shareit.ShareItServer;
import practicum.ru.shareit.exceptions.NotFoundException;
import practicum.ru.shareit.request.Request;
import practicum.ru.shareit.request.RequestRepository;
import practicum.ru.shareit.request.RequestServiceImpl;
import practicum.ru.shareit.request.dto.RequestDto;
import practicum.ru.shareit.request.dto.RequestMapper;
import practicum.ru.shareit.user.User;
import practicum.ru.shareit.user.dto.UserMapper;

import java.time.LocalDateTime;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@ContextConfiguration(classes = ShareItServer.class)
@Import({RequestServiceImpl.class})
@ActiveProfiles("test")
public class RequestServiceImplTest {
    @Autowired
    private RequestServiceImpl requestService;
    @Autowired
    private RequestRepository requestRepository;
    @Autowired
    private TestEntityManager entityManager;
    private Request request;
    private User user;

    @BeforeEach
    void setUp() {
        requestRepository.deleteAll();
        entityManager.flush();
        entityManager.clear();

        user = new User();
        user.setName("Silas");
        user.setEmail("silas_eason@mail.com");
        user = entityManager.persistAndFlush(user);

        request = new Request();
        request.setDescription("Request");
        request.setRequestor(user);
        request.setCreated(LocalDateTime.now());
        request = entityManager.persistAndFlush(request);
    }

    @Test
    void addRequest() {
        RequestDto requestDto = new RequestDto(null, "Request", null, null, null);
        RequestDto createdRequestDto = requestService.addRequest(user.getId(), requestDto);

        assertThat(createdRequestDto.getId()).isNotNull();
        assertThat(createdRequestDto.getDescription()).isEqualTo("Request");
        assertThat(createdRequestDto.getRequestor()).isEqualTo(UserMapper.toUserDto(user));
        assertThat(createdRequestDto.getCreated()).isNotNull();

        Request savedRequest = entityManager.find(Request.class, createdRequestDto.getId());
        assertThat(savedRequest).isNotNull();
        assertThat(savedRequest.getDescription()).isEqualTo("Request");
        assertThat(savedRequest.getRequestor()).isEqualTo(user);
        assertThat(savedRequest.getCreated()).isNotNull();
    }

    @Test
    void addRequestNotExistingUser() {
        RequestDto requestDto = new RequestDto(null, "Request", null, null, null);
        entityManager.remove(user);

        final NotFoundException exception = assertThrows(NotFoundException.class,
                () -> requestService.addRequest(user.getId(), requestDto));
        assertEquals("Пользователь с ID " + user.getId() + " не найден", exception.getMessage());
    }

    @Test
    void findUserRequests() {
        Collection<RequestDto> userRequests = requestService.getUserRequests(user.getId());
        assertThat(userRequests.size()).isEqualTo(1);
        assertThat(userRequests.contains(RequestMapper.toRequestDto(request))).isTrue();
    }

    @Test
    void findNotExistingUserRequests() {
        entityManager.remove(user);

        final NotFoundException exception = assertThrows(NotFoundException.class,
                () -> requestService.getUserRequests(user.getId()));
        assertEquals("Пользователь с ID " + user.getId() + " не найден", exception.getMessage());
    }

    @Test
    void findUserRequestsEmpty() {
        requestRepository.deleteAll();
        Collection<RequestDto> userRequests = requestService.getUserRequests(user.getId());
        assertThat(userRequests.size()).isEqualTo(0);
    }

    @Test
    void getAllRequests() {
        Collection<RequestDto> allRequests = requestService.getAllRequests();
        assertThat(allRequests.size()).isEqualTo(1);
        assertThat(allRequests.contains(RequestMapper.toRequestDto(request))).isTrue();
    }

    @Test
    void getAllRequestsEmpty() {
        requestRepository.deleteAll();
        Collection<RequestDto> allRequests = requestService.getAllRequests();
        assertThat(allRequests.size()).isEqualTo(0);
    }

    @Test
    void findById() {
        RequestDto foundRequestDto = requestService.findById(request.getId());
        assertThat(foundRequestDto).isNotNull();
        assertThat(foundRequestDto.getRequestor()).isEqualTo(UserMapper.toUserDto(user));
        assertThat(foundRequestDto.getDescription()).isEqualTo("Request");
    }

    @Test
    void findByIdNotExistingRequest() {
        requestRepository.delete(request);

        final NotFoundException exception = assertThrows(NotFoundException.class,
                () -> requestService.findById(request.getId()));
        assertEquals("Запрос с ID " + request.getId() + " не найден", exception.getMessage());
    }
}
