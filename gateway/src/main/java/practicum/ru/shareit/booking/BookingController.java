package practicum.ru.shareit.booking;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import practicum.ru.shareit.booking.dto.BookingDto;
import practicum.ru.shareit.validation.Marker;

@Validated
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingClient bookingClient;
    private final String userIdHeader = "X-Sharer-User-Id";

    @Autowired
    public BookingController(BookingClient bookingClient) {
        this.bookingClient = bookingClient;
    }

    @PostMapping
    @Validated({Marker.OnCreate.class})
    public ResponseEntity<Object> addBooking(@RequestHeader(userIdHeader) Long userId,
                                             @Valid @RequestBody BookingDto bookingDto) {
        return bookingClient.addBooking(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> ownerBookingApprove(@RequestHeader(userIdHeader) Long userId,
                                                @PathVariable Long bookingId,
                                                @RequestParam boolean approved) {
        return bookingClient.ownerApprove(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@RequestHeader(userIdHeader) Long userId,
                                       @PathVariable Long bookingId) {
        return bookingClient.getBooking(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getUserBooking(@RequestHeader(userIdHeader) Long userId,
                                                       @RequestParam(defaultValue = "all") String state) {
        return bookingClient.getUserBooking(userId, state);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getOwnerBooking(@RequestHeader(userIdHeader) Long userId,
                                                        @RequestParam(defaultValue = "all") String state) {
        return bookingClient.getOwnerBooking(userId, state);
    }
}
