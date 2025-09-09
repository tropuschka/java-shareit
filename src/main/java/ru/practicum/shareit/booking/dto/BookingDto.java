package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.validation.Marker;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class BookingDto {
    private Long id;
    private Long item;
    private Long booker;
    @NotNull(groups = Marker.OnCreate.class, message = "Дата начала бронирования должна быть указана")
    private LocalDateTime start;
    @NotNull(groups = Marker.OnCreate.class, message = "Дата окончания бронирования должна быть указана")
    private LocalDateTime end;
    private BookingStatus status;
}
