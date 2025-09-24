package practicum.ru.shareit.booking;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import practicum.ru.shareit.booking.dto.BookingDto;
import practicum.ru.shareit.booking.dto.ReturnBookingDto;
import practicum.ru.shareit.validation.Marker;

import java.util.Collection;

/**
 * TODO Sprint add-bookings.
 */
@Validated
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;
    private final String userIdHeader = "X-Sharer-User-Id";

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    @Validated({Marker.OnCreate.class})
    public ReturnBookingDto addBooking(@RequestHeader(userIdHeader) Long userId,
                                       @Valid @RequestBody BookingDto bookingDto) {
        return bookingService.addBooking(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public ReturnBookingDto ownerBookingApprove(@RequestHeader(userIdHeader) Long userId,
                                                @PathVariable Long bookingId,
                                          @RequestParam boolean approved) {
        return bookingService.ownerApprove(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ReturnBookingDto getBooking(@RequestHeader(userIdHeader) Long userId,
                                       @PathVariable Long bookingId) {
        return bookingService.getBooking(userId, bookingId);
    }

    @GetMapping
    public Collection<ReturnBookingDto> getUserBooking(@RequestHeader(userIdHeader) Long userId,
                                                       @RequestParam(defaultValue = "all") String state) {
        return bookingService.getUserBooking(userId, state);
    }

    @GetMapping("/owner")
    public Collection<ReturnBookingDto> getOwnerBooking(@RequestHeader(userIdHeader) Long userId,
                                                        @RequestParam(defaultValue = "all") String state) {
        return bookingService.getOwnerBooking(userId, state);
    }
}
