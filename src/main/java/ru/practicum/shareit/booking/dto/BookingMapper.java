package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.item.Item;

public class BookingMapper {
    public static BookingDto toBookingDto(Booking booking) {
        return new BookingDto(
                booking.getId(),
                booking.getItem().getId(),
                booking.getBooker(),
                booking.getStart(),
                booking.getEnd(),
                booking.getStatus()
        );
    }

    public static Booking toBooking(BookingDto bookingDto, Item item) {
        Booking booking = new Booking();
        booking.setItem(item);
        booking.setBooker(bookingDto.getBooker());
        booking.setStart(bookingDto.getStart());
        booking.setEnd(bookingDto.getEnd());
        booking.setStatus(bookingDto.getStatus());
        return booking;
    }
}
