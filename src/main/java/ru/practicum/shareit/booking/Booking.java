package ru.practicum.shareit.booking;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Data
@RequiredArgsConstructor
public class Booking {
    private Long id;
    private Long item;
    private Long booker;
    private LocalDateTime start;
    private LocalDateTime end;
    private BookingStatus status;
}
