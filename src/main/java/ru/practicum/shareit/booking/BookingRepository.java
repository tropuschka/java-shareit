package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByBooker(Long userId);

    List<Booking> findByBookerAndStatus(Long userId, BookingStatus status);

    List<Booking> findByBookerAndStartIsBeforeAndEndIsAfter(Long userId, LocalDateTime now, LocalDateTime now1);

    List<Booking> findByBookerAndEndIsBefore(Long userId, LocalDateTime now);

    List<Booking> findByBookerAndStartIsAfter(Long userId, LocalDateTime now);

    List<Booking> findByItemOwner(Long userId);

    List<Booking> findByItemOwnerAndStatus(Long userId, BookingStatus status);

    List<Booking> findByItemOwnerAndStartIsBeforeAndEndIsAfter(Long userId, LocalDateTime now, LocalDateTime now1);

    List<Booking> findByItemOwnerAndEndIsBefore(Long userId, LocalDateTime now);

    List<Booking> findByItemOwnerAndStartIsAfter(Long userId, LocalDateTime now);
}
