package practicum.ru.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByBookerId(Long userId);

    List<Booking> findByBookerIdAndStatus(Long userId, BookingStatus status);

    List<Booking> findByBookerIdAndStartIsBeforeAndEndIsAfter(Long userId, LocalDateTime now, LocalDateTime now1);

    List<Booking> findByBookerIdAndEndIsBefore(Long userId, LocalDateTime now);

    List<Booking> findByBookerIdAndStartIsAfter(Long userId, LocalDateTime now);

    List<Booking> findByItemOwner(Long userId);

    List<Booking> findByItemOwnerAndStatus(Long userId, BookingStatus status);

    List<Booking> findByItemOwnerAndStartIsBeforeAndEndIsAfter(Long userId, LocalDateTime now, LocalDateTime now1);

    List<Booking> findByItemOwnerAndEndIsBefore(Long userId, LocalDateTime now);

    List<Booking> findByItemOwnerAndStartIsAfter(Long userId, LocalDateTime now);

    List<Booking> findByItemId(Long itemId);

    List<Booking> findByItemIdAndStatus(Long itemId, BookingStatus bookingStatus);

    List<Booking> findByItemIdIn(List<Long> items);
}
