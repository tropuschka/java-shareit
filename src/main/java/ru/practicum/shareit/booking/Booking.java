package ru.practicum.shareit.booking;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.item.Item;

import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
@Data
@RequiredArgsConstructor
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;
    @Column(name = "booker_id", nullable = false)
    private Long booker;
    @Column(name = "start_date", nullable = false)
    private LocalDateTime start;
    @Column(name = "end_date", nullable = false)
    private LocalDateTime end;
    @Column(name = "status", nullable = false)
    private BookingStatus status;
}
