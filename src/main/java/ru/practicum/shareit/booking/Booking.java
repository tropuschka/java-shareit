package ru.practicum.shareit.booking;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

@Data
@RequiredArgsConstructor
public class Booking {
    private Long id;
    private Long item;
    private Long booker;
    private LocalDate start;
    private LocalDate end;
    private BookingStatus status;
}
