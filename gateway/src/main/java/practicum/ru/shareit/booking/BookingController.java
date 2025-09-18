package practicum.ru.shareit.booking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import practicum.ru.shareit.booking.dto.BookingDto;
import practicum.ru.shareit.validation.Marker;

import java.util.Collection;

@Validated
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingClient bookingClient;
    private final String USER_ID_HEADER = "X-Sharer-User-Id";


    @PostMapping
    @Validated({Marker.OnCreate.class})
    public ResponseEntity<Object> addBooking(@RequestHeader(USER_ID_HEADER) Long userId,
                                             @Valid @RequestBody BookingDto bookingDto) {
        return bookingClient.addBooking(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> ownerBookingApprove(@RequestHeader(USER_ID_HEADER) Long userId,
                                                @PathVariable Long bookingId,
                                                @RequestParam boolean approved) {
        return bookingClient.ownerApprove(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@RequestHeader(USER_ID_HEADER) Long userId,
                                       @PathVariable Long bookingId) {
        return bookingClient.getBooking(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getUserBooking(@RequestHeader(USER_ID_HEADER) Long userId,
                                                       @RequestParam(defaultValue = "all") String state) {
        return bookingClient.getUserBooking(userId, state);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getOwnerBooking(@RequestHeader(USER_ID_HEADER) Long userId,
                                                        @RequestParam(defaultValue = "all") String state) {
        return bookingClient.getOwnerBooking(userId, state);
    }
}
