package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.BookingStatus;

import java.time.LocalDate;

public class BookingDto {
    private Long id;
    private Long item;
    private Long booker;
    private LocalDate start;
    private LocalDate end;
    private BookingStatus status;
}
