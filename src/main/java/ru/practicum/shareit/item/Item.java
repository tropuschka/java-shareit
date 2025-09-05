package ru.practicum.shareit.item;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Entity
@Table(name = "Item")
@Data
@RequiredArgsConstructor
public class Item {
    @Id
    private Long id;
    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "description", nullable = false)
    private String description;
    @Column(name = "owner_id", nullable = false)
    private Long owner;
    @Column(name = "available", nullable = false)
    private Boolean available;
}
