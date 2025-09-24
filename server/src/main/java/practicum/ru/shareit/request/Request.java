package practicum.ru.shareit.request;

import jakarta.persistence.*;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import practicum.ru.shareit.item.Item;
import practicum.ru.shareit.user.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "requests")
@Data
@RequiredArgsConstructor
public class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    @Column(name = "description", nullable = false)
    private String description;
    @ManyToOne
    @JoinColumn(name = "requestor_id", nullable = false)
    private User requestor;
    @Column(name = "created", nullable = false)
    private LocalDateTime created;
}
