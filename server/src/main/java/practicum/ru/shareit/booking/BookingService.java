package practicum.ru.shareit.booking;

import practicum.ru.shareit.booking.dto.BookingDto;
import practicum.ru.shareit.booking.dto.ReturnBookingDto;

import java.util.Collection;

public interface BookingService {
    ReturnBookingDto addBooking(Long userId, BookingDto bookingDto);

    ReturnBookingDto ownerApprove(Long userId, Long bookingId, boolean approved);

    ReturnBookingDto getBooking(Long userId, Long bookingId);

    Collection<ReturnBookingDto> getUserBooking(Long userId, String status);

    Collection<ReturnBookingDto> getOwnerBooking(Long userId, String status);
}
