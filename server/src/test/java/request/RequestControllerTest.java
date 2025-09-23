package request;

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
import practicum.ru.shareit.request.RequestController;
import practicum.ru.shareit.request.RequestService;
import practicum.ru.shareit.request.dto.RequestDto;
import practicum.ru.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class RequestControllerTest {
    @Mock
    private RequestService requestService;
    @InjectMocks
    private RequestController requestController;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private MockMvc mockMvc;
    private RequestDto requestDto;
    private RequestDto returnRequestDto;
    private User requestor = new User();
    private final String USER_ID_HEADER = "X-Sharer-User-Id";

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(requestController).build();
        requestor.setId(1L);
        requestor.setName("Foreydoun");
        requestor.setEmail("foreydoun@mail.com");
        requestDto = new RequestDto(null, "Description", null, null, null);
        returnRequestDto = new RequestDto(1L, "Description", requestor, LocalDateTime.now(), null);
    }

    @Test
    public void createRequest() throws Exception {
        when(requestService.addRequest(anyLong(), any(RequestDto.class))).thenReturn(returnRequestDto);

        mockMvc.perform(post("/requests")
                        .header(USER_ID_HEADER, 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
        verify(requestService, times(1)).addRequest(1L, requestDto);
    }

    @Test
    public void createRequestWithoutDescription() throws Exception {
        requestDto = new RequestDto(null, null, null, null, null);
        when(requestService.addRequest(anyLong(), any(RequestDto.class)))
                .thenThrow(new ConditionsNotMetException("Описание запроса не должно быть пустым"));

        final ConditionsNotMetException exception = Assertions.assertThrows(ConditionsNotMetException.class,
                () -> requestService.addRequest(1L, requestDto));
        Assertions.assertEquals("Описание запроса не должно быть пустым", exception.getMessage());
    }

    @Test
    public void getUserRequest() throws Exception {
        when(requestService.getUserRequests(anyLong())).thenReturn(List.of(returnRequestDto));

        mockMvc.perform(get("/requests")
                .header(USER_ID_HEADER, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].description").value("Description"));
        verify(requestService, times(1)).getUserRequests(1L);
    }

    @Test
    public void getAllRequests() throws Exception {
        when(requestService.getAllRequests()).thenReturn(List.of(returnRequestDto));

        mockMvc.perform(get("/requests/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].description").value("Description"));
        verify(requestService, times(1)).getAllRequests();
    }

    @Test
    public void getRequestById() throws Exception
    {
        when(requestService.findById(anyLong())).thenReturn(returnRequestDto);
        mockMvc.perform(get("/requests/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.description").value("Description"));
        verify(requestService, times(1)).findById(1L);
    }}
