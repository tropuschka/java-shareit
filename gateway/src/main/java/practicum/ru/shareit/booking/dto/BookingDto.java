package practicum.ru.shareit.booking.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import practicum.ru.shareit.booking.BookingStatus;
import practicum.ru.shareit.validation.Marker;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class BookingDto {
    private Long id;
    @NotNull(groups = Marker.OnCreate.class, message = "Бронируемый предмет должен быть указан")
    private Long itemId;
    private Long booker;
    @NotNull(groups = Marker.OnCreate.class, message = "Дата начала бронирования должна быть указана")
    private LocalDateTime start;
    @NotNull(groups = Marker.OnCreate.class, message = "Дата окончания бронирования должна быть указана")
    private LocalDateTime end;
    private BookingStatus status;
}
