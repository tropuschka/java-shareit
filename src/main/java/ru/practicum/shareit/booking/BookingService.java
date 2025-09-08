package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.Collection;

public interface BookingService {
    BookingDto addBooking(Long userId, BookingDto bookingDto);

    BookingDto ownerApprove(Long userId, Long bookingId, boolean approved);

    BookingDto getBooking(Long userId, Long bookingId);

    Collection<BookingDto> getUserBooking(Long userId, String status);

    Collection<BookingDto> getOwnerBooking(Long userId, String status);
}
