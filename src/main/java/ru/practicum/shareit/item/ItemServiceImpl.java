package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exceptions.ConditionsNotMetException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    public ItemDto addItem(Long userId, ItemDto itemDto) {
        checkUser(userId);
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(userId);
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
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
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public Map<ItemDto, List<Comment>> getItemDtoById(Long itemId) {
        ItemDto itemDto = ItemMapper.toItemDto(itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Предмет с ID " + itemId + " не найден")));
        List<Comment> itemComments = commentRepository.findByItemId(itemId);
        Map<ItemDto, List<Comment>> itemWithComments = new HashMap<>();
        itemWithComments.put(itemDto, itemComments);
        return itemWithComments;
    }

    @Override
    public Map<ItemDtoWithBooking, List<Comment>> getUserItems(Long userId) {
        checkUser(userId);
        Set<ItemDtoWithBooking> userItems = itemRepository.findByOwner(userId).stream()
                .map(ItemMapper::toItemDtoWithBooking)
                .collect(Collectors.toSet());

        LocalDateTime now = LocalDateTime.now();
        Map<ItemDtoWithBooking, List<Comment>> itemsWithComments = new HashMap<>();
        for (ItemDtoWithBooking itemDto:userItems) {
            List<Booking> itemBooking = bookingRepository.findByItemId(itemDto.getId());
            Optional<Booking> lastBookingOpt = itemBooking.stream()
                    .filter(b -> b.getStart().isBefore(now))
                    .max(Comparator.comparing(Booking::getEnd));
            lastBookingOpt.ifPresent(booking -> itemDto.setLastBooking(booking.getId()));

            Optional<Booking> nextBookingOpt = itemBooking.stream()
                    .filter(b -> b.getStart().isAfter(now))
                    .min(Comparator.comparing(Booking::getStart));
            nextBookingOpt.ifPresent(booking -> itemDto.setNextBooking(booking.getId()));

            itemsWithComments.put(itemDto, commentRepository.findByItemId(itemDto.getId()));
        }
        return itemsWithComments;
    }

    @Override
    public Collection<ItemDto> searchItem(String query) {
        if (query.isBlank()) return new ArrayList<>();
        return itemRepository.search(query).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toSet());
    }

    @Override
    public CommentDto addComment(Long userId, CommentDto commentDto) {
        checkUser(userId);
        getItemById(commentDto.getItemId());
        List<Booking> itemBooking = bookingRepository.findByItemId(commentDto.getItemId());
        boolean isBooker = itemBooking.stream()
                .filter(b -> b.getStart().isBefore(LocalDateTime.now()))
                .anyMatch(b -> b.getBooker().equals(userId));
        if (!isBooker) throw new ConditionsNotMetException("Оставлять комментарии можно только после аренды");
        Comment comment = CommentMapper.toComment(commentDto);
        comment.setAuthorId(userId);
        return CommentMapper.toCommentDto(commentRepository.save(comment));
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
