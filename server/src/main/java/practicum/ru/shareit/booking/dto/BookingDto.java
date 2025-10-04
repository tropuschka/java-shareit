package practicum.ru.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import practicum.ru.shareit.booking.BookingStatus;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class BookingDto {
    private Long id;
    private Long itemId;
    private Long booker;
    private LocalDateTime start;
    private LocalDateTime end;
    private BookingStatus status;
}
