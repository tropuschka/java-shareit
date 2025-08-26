package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.ConditionsNotMetException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.UserRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository,
                           UserRepository userRepository,
                           ItemRequestRepository itemRequestRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.itemRequestRepository = itemRequestRepository;
    }

    public ItemDto addItem(Long userId, ItemDto itemDto) {
        checkUser(userId);
        validateItemDto(itemDto);
        Item item = ItemMapper.toItem(itemDto);
        setItemRequest(itemDto, item);
        item.setOwner(userId);
        itemRepository.createItem(item);
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
        setItemRequest(itemDto, item);
        itemRepository.updateItem(itemId, item);
        return ItemMapper.toItemDto(item);
    }

    public ItemDto getItemDtoById(Long itemId) {
        return ItemMapper.toItemDto(itemRepository.getItemById(itemId)
                .orElseThrow(() -> new NotFoundException("Предмет с ID " + itemId + " не найден")));
    }

    public Collection<ItemDto> getUserItems(Long userId) {
        checkUser(userId);
        return itemRepository.getUserItems(userId).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toSet());
    }

    public Collection<ItemDto> searchItem(String query) {
        if (query.isBlank()) return new ArrayList<>();
        return itemRepository.searchItem(query).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toSet());
    }

    private Item getItemById(Long itemId) {
        return itemRepository.getItemById(itemId)
                .orElseThrow(() -> new NotFoundException("Предмет с ID " + itemId + " не найден"));
    }

    private void validateItemDto(ItemDto itemDto) {
        if (itemDto.getName() == null || itemDto.getName().isBlank()) {
            throw new ConditionsNotMetException("Название предмета не должно быть пустым");
        }
        if (itemDto.getAvailable() != true && itemDto.getAvailable() != false) {
            throw new ConditionsNotMetException("Статус должен быть указан");
        }
        if (itemDto.getDescription() == null || itemDto.getDescription().isBlank()) {
            throw new ConditionsNotMetException("Описание предмета не должно быть пустым");
        }
    }

    private void checkUser(Long userId) {
        userRepository.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + userId + " не найден"));
    }

    private void checkOwner(Long userId, Item item) {
        if (!Objects.equals(userId, item.getOwner()) || item.getOwner() == null) {
            throw new ConditionsNotMetException("Пользователь с ID " + userId +
                    " не является владельцем предмета с ID " + item.getId());
        }
    }

    private void setItemRequest(ItemDto itemDto, Item item) {
        if (itemDto.getRequest() != null) {
            item.setRequest(itemRequestRepository.getRequestById(itemDto.getRequest())
                    .orElseThrow(() -> new NotFoundException("Запрос с ID " + itemDto.getRequest() + " не найден")));
        }
    }
}
