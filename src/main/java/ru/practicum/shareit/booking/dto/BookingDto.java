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
    @NotNull(groups = Marker.OnCreate.class, message = "ID забронированного предмета должен быть указан")
    private Long item;
    @NotNull(groups = Marker.OnCreate.class, message = "ID пользователя, бронирующего предмет, доложен быть указан")
    private Long booker;
    @NotNull(groups = Marker.OnCreate.class, message = "Дата начала бронирования должна быть указана")
    private LocalDateTime start;
    @NotNull(groups = Marker.OnCreate.class, message = "Дата окончания бронирования должна быть указана")
    private LocalDateTime end;
    @NotNull(groups = Marker.OnCreate.class, message = "Статут бронирования должен быть указан")
    private BookingStatus status;
}
