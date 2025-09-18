package practicum.ru.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import practicum.ru.shareit.booking.BookingStatus;
import practicum.ru.shareit.item.Item;
import practicum.ru.shareit.user.User;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ReturnBookingDto {
    private Long id;
    private Item item;
    private User booker;
    private LocalDateTime start;
    private LocalDateTime end;
    private BookingStatus status;
}