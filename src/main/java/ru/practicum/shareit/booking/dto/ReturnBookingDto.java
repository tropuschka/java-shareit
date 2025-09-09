package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.validation.Marker;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ReturnBookingDto {
    private Long id;
    @NotNull(groups = Marker.OnCreate.class, message = "Бронируемый предмет должен быть указан")
    private Item item;
    private User booker;
    @NotNull(groups = Marker.OnCreate.class, message = "Дата начала бронирования должна быть указана")
    private LocalDateTime start;
    @NotNull(groups = Marker.OnCreate.class, message = "Дата окончания бронирования должна быть указана")
    private LocalDateTime end;
    private BookingStatus status;
}