package booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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
import practicum.ru.shareit.booking.BookingController;
import practicum.ru.shareit.booking.BookingService;
import practicum.ru.shareit.booking.BookingStatus;
import practicum.ru.shareit.booking.dto.BookingDto;
import practicum.ru.shareit.booking.dto.ReturnBookingDto;
import practicum.ru.shareit.exceptions.ConditionsNotMetException;
import practicum.ru.shareit.item.Item;
import practicum.ru.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class BookingControllerTest {
    @Mock
    private BookingService bookingService;
    @InjectMocks
    private BookingController bookingController;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private MockMvc mockMvc;
    private BookingDto bookingDto;
    private ReturnBookingDto returnBookingDto;
    private final String USER_ID_HEADER = "X-Sharer-User-Id";
    private Item item;
    private User user;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(bookingController).build();
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.registerModule(new JavaTimeModule());
        LocalDateTime now = LocalDateTime.now();
        item = new Item();
        item.setId(1L);
        item.setName("Item");
        item.setOwner(1L);
        item.setDescription("Description");
        item.setAvailable(true);
        user = new User();
        user.setId(1L);
        user.setName("Hugo");
        user.setEmail("hugo@mail.com");
        bookingDto = new BookingDto(null, 1L, 1L, now, now.plusHours(1), null);
        returnBookingDto = new ReturnBookingDto(1L, item, user, now, now.plusHours(1), BookingStatus.WAITING);
    }

    @Test
    public void addBooking() throws Exception {
        when(bookingService.addBooking(anyLong(), any(BookingDto.class))).thenReturn(returnBookingDto);

        mockMvc.perform(post("/bookings")
                .header(USER_ID_HEADER, 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bookingDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
        verify(bookingService, times(1)).addBooking(1L, bookingDto);
    }

    @Test
    public void addBookingWithoutItem() throws Exception {
        bookingDto.setItemId(null);
        when(bookingService.addBooking(anyLong(), any(BookingDto.class)))
                .thenThrow(new ConditionsNotMetException("Бронируемый предмет должен быть указан"));

        final ConditionsNotMetException exception = Assertions.assertThrows(ConditionsNotMetException.class,
                () -> bookingService.addBooking(1L, bookingDto));
        Assertions.assertEquals("Бронируемый предмет должен быть указан", exception.getMessage());
    }

    @Test
    public void addBookingWithoutStart() throws Exception {
        bookingDto.setStart(null);
        when(bookingService.addBooking(anyLong(), any(BookingDto.class)))
                .thenThrow(new ConditionsNotMetException("Дата начала бронирования должна быть указана"));

        final ConditionsNotMetException exception = Assertions.assertThrows(ConditionsNotMetException.class,
                () -> bookingService.addBooking(1L, bookingDto));
        Assertions.assertEquals("Дата начала бронирования должна быть указана", exception.getMessage());
    }

    @Test
    public void addBookingWithoutEnd() throws Exception {
        bookingDto.setEnd(null);
        when(bookingService.addBooking(anyLong(), any(BookingDto.class)))
                .thenThrow(new ConditionsNotMetException("Дата окончания бронирования должна быть указана"));

        final ConditionsNotMetException exception = Assertions.assertThrows(ConditionsNotMetException.class,
                () -> bookingService.addBooking(1L, bookingDto));
        Assertions.assertEquals("Дата окончания бронирования должна быть указана", exception.getMessage());
    }

    @Test
    public void ownerBookingApprove() throws Exception {
        returnBookingDto.setStatus(BookingStatus.APPROVED);
        when(bookingService.ownerApprove(anyLong(), anyLong(), anyBoolean())).thenReturn(returnBookingDto);

        mockMvc.perform(patch("/bookings/1")
                .header(USER_ID_HEADER, 1)
                .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
        verify(bookingService, times(1)).ownerApprove(1L, 1L, true);
    }

    @Test
    public void getBooking() throws Exception {
        when(bookingService.getBooking(anyLong(), anyLong())).thenReturn(returnBookingDto);

        mockMvc.perform(get("/bookings/1")
                .header(USER_ID_HEADER, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
        verify(bookingService, times(1)).getBooking(1L, 1L);
    }

    @Test
    public void getUserBooking() throws Exception {
        when(bookingService.getUserBooking(anyLong(), anyString())).thenReturn(List.of(returnBookingDto));

        mockMvc.perform(get("/bookings")
                .header(USER_ID_HEADER, 1)
                .param("state", "all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1L));
        verify(bookingService, times(1)).getUserBooking(1L, "all");
    }

    @Test
    public void getOwnerBooking() throws Exception {
        when(bookingService.getOwnerBooking(anyLong(), anyString())).thenReturn(List.of(returnBookingDto));

        mockMvc.perform(get("/bookings/owner")
                        .header(USER_ID_HEADER, 1)
                        .param("state", "all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1L));
        verify(bookingService, times(1)).getOwnerBooking(1L, "all");
    }
}
