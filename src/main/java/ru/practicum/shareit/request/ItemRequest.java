package ru.practicum.shareit.request;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

@Data
@RequiredArgsConstructor
public class ItemRequest {
    private Long id;
    private String description;
    private Long requestor;
    private LocalDate created;
}
