package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface ItemService {
    ItemDto addItem(Long userId, ItemDto itemDto);

    ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto);

    Map<ItemDto, List<Comment>> getItemDtoById(Long itemId);

    Map<ItemDtoWithBooking, List<Comment>> getUserItems(Long userId);

    Collection<ItemDto> searchItem(String query);

    CommentDto addComment(Long userId, CommentDto commentDto);
}
