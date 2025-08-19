package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.ConditionsNotMetException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.UserRepository;

@Service
public class ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Autowired
    public ItemService(ItemRepository itemRepository,
                       UserRepository userRepository,
                       ItemRequestRepository itemRequestRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.itemRequestRepository = itemRequestRepository;
    }

    public Item addItem(Long userId, ItemDto itemDto) {
        checkUser(userId);
        verifyItemDto(itemDto);
        Item item = ItemMapper.toItem(itemDto);
        if (itemDto.getRequest() != null) {
            item.setRequest(itemRequestRepository.getRequestById(itemDto.getRequest())
                    .orElseThrow(() -> new NotFoundException("Запрос с ID " + itemDto.getRequest() + " не найден")));
        }
        item.setOwner(userId);
        itemRepository.createItem(userId, item);
        return item;
    }

    private void verifyItemDto(ItemDto itemDto) {
        if (itemDto.getName() == null || itemDto.getName().isBlank()) {
            throw new ConditionsNotMetException("Название предмета не должно быть пустым");
        }
    }

    private void checkUser(Long userId) {
        userRepository.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + userId + " не найден"));
    }
}
