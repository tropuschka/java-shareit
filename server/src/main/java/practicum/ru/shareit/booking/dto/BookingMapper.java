package practicum.ru.shareit.booking.dto;

import practicum.ru.shareit.booking.Booking;
import practicum.ru.shareit.booking.BookingStatus;
import practicum.ru.shareit.item.Item;
import practicum.ru.shareit.user.User;

public class BookingMapper {
    public static BookingDto toBookingDto(Booking booking) {
        return new BookingDto(
                booking.getId(),
                booking.getItem().getId(),
                booking.getBooker().getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getStatus()
        );
    }

    public static ReturnBookingDto toReturnBookingDto(Booking booking) {
        return new ReturnBookingDto(
                booking.getId(),
                booking.getItem(),
                booking.getBooker(),
                booking.getStart(),
                booking.getEnd(),
                booking.getStatus()
        );
    }

    public static Booking toBooking(BookingDto bookingDto, Item item, User booker) {
        if (bookingDto.getStatus() == null) bookingDto.setStatus(BookingStatus.WAITING);
        Booking booking = new Booking();
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStart(bookingDto.getStart());
        booking.setEnd(bookingDto.getEnd());
        booking.setStatus(bookingDto.getStatus());
        return booking;
    }
}
