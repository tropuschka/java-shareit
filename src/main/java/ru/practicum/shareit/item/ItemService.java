package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;

import java.util.Collection;

public interface ItemService {
    ItemDto addItem(Long userId, ItemDto itemDto);

    ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto);

    ItemDtoWithBooking getItemDtoById(Long userId, Long itemId);

    Collection<ItemDtoWithBooking> getUserItems(Long userId);

    Collection<ItemDto> searchItem(String query);

    CommentDto addComment(Long userId, Long itemId, CommentDto commentDto);
}
