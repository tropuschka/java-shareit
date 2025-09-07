package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.ConditionsNotMetException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.user.UserRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    public ItemDto addItem(Long userId, ItemDto itemDto) {
        checkUser(userId);
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(userId);
        itemRepository.save(item);
        return ItemMapper.toItemDto(item);
    }

    public ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto) {
        checkUser(userId);
        getItemById(itemId);
        Item item = getItemById(itemId);
        checkOwner(userId, item);
        if (itemDto.getName() != null && !itemDto.getName().isBlank()) item.setName(itemDto.getName());
        if (itemDto.getDescription() != null && !itemDto.getDescription().isBlank()) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        itemRepository.save(item);
        return ItemMapper.toItemDto(item);
    }

    public ItemDto getItemDtoById(Long itemId) {
        return ItemMapper.toItemDto(itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Предмет с ID " + itemId + " не найден")));
    }

    public Collection<ItemDto> getUserItems(Long userId) {
        checkUser(userId);
        return itemRepository.findByOwnerId(userId).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toSet());
    }

    public Collection<ItemDto> searchItem(String query) {
        if (query.isBlank()) return new ArrayList<>();
        return itemRepository.search(query).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toSet());
    }

    private Item getItemById(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Предмет с ID " + itemId + " не найден"));
    }

    private void checkUser(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + userId + " не найден"));
    }

    private void checkOwner(Long userId, Item item) {
        if (!Objects.equals(userId, item.getOwner()) || item.getOwner() == null) {
            throw new ConditionsNotMetException("Пользователь с ID " + userId +
                    " не является владельцем предмета с ID " + item.getId());
        }
    }
}
