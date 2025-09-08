package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.validation.Marker;

import java.util.Collection;

/**
 * TODO Sprint add-bookings.
 */
@Validated
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    @Validated({Marker.OnCreate.class})
    public BookingDto addBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                 @Valid @RequestBody BookingDto bookingDto) {
        return bookingService.addBooking(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto ownerBookingApprove(@RequestHeader("X-Sharer-User-Id") Long userId,
                                          @PathVariable Long bookingId,
                                          @RequestParam boolean approved) {
        return bookingService.ownerApprove(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBooking(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long bookingId) {
        return bookingService.getBooking(userId, bookingId);
    }

    @GetMapping
    public Collection<BookingDto> getUserBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                              @RequestParam(defaultValue = "all") String state) {
        return bookingService.getUserBooking(userId, state);
    }

    @GetMapping("/owner")
    public Collection<BookingDto> getOwnerBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                 @RequestParam(defaultValue = "all") String state) {
        return bookingService.getOwnerBooking(userId, state);
    }
}
