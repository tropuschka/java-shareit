package ru.practicum.shareit.request.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ItemRequestDto {
    private Long id;
    private String description;
    private Long requestor;
    private LocalDate created;
}
