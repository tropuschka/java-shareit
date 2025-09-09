package ru.practicum.shareit.item;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Entity
@Table(name = "comments")
@Data
@RequiredArgsConstructor
public class Comment {
    @Id
    private Long id;
    @Column(name = "text", nullable = false)
    private String text;
    @Column(name = "item_id", nullable = false)
    private Long itemId;
    @Column(name = "author_id", nullable = false)
    private Long authorId;
}
