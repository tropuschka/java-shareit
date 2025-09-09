package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.exceptions.ConditionsNotMetException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.user.User;
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
    public ItemDtoWithBooking getItemDtoById(Long userId, Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Предмет с ID " + itemId + " не найден"));
        ItemDtoWithBooking itemDto = ItemMapper.toItemDtoWithBooking(item);

        if (Objects.equals(userId, item.getOwner())) {
            LocalDateTime now = LocalDateTime.now();
            List<Booking> itemBooking = bookingRepository.findByItemId(itemDto.getId());
            Optional<Booking> lastBookingOpt = itemBooking.stream()
                    .filter(b -> b.getStart().isBefore(now))
                    .filter(b -> b.getStatus().equals(BookingStatus.APPROVED))
                    .max(Comparator.comparing(Booking::getEnd));
            lastBookingOpt.ifPresent(booking -> itemDto.setLastBooking(booking.getId()));

            Optional<Booking> nextBookingOpt = itemBooking.stream()
                    .filter(b -> b.getStart().isAfter(now))
                    .min(Comparator.comparing(Booking::getStart));
            nextBookingOpt.ifPresent(booking -> itemDto.setNextBooking(booking.getId()));
        } else {
            itemDto.setNextBooking(null);
            itemDto.setLastBooking(null);
        }

        List<Comment> itemComments = commentRepository.findByItemId(itemId);
        List<CommentDto> commentDto = new ArrayList<>();
        for (Comment comment:itemComments) {
            commentDto.add(CommentMapper.toCommentDto(comment));
        }
        itemDto.setComments(commentDto);
        return itemDto;
    }

    @Override
    public Collection<ItemDtoWithBooking> getUserItems(Long userId) {
        checkUser(userId);
        Set<ItemDtoWithBooking> userItems = itemRepository.findByOwner(userId).stream()
                .map(ItemMapper::toItemDtoWithBooking)
                .collect(Collectors.toSet());

        LocalDateTime now = LocalDateTime.now();
        for (ItemDtoWithBooking itemDto:userItems) {
            List<Booking> itemBooking = bookingRepository.findByItemId(itemDto.getId());
            Optional<Booking> lastBookingOpt = itemBooking.stream()
                    .filter(b -> b.getStart().isBefore(now))
                    .filter(b -> b.getStatus().equals(BookingStatus.APPROVED))
                    .max(Comparator.comparing(Booking::getEnd));
            lastBookingOpt.ifPresent(booking -> itemDto.setLastBooking(booking.getId()));

            Optional<Booking> nextBookingOpt = itemBooking.stream()
                    .filter(b -> b.getStart().isAfter(now))
                    .min(Comparator.comparing(Booking::getStart));
            nextBookingOpt.ifPresent(booking -> itemDto.setNextBooking(booking.getId()));

            List<Comment> itemComments = commentRepository.findByItemId(itemDto.getId());
            List<CommentDto> commentDto = new ArrayList<>();
            for (Comment comment:itemComments) {
                commentDto.add(CommentMapper.toCommentDto(comment));
            }
            itemDto.setComments(commentDto);
        }
        return userItems;
    }

    @Override
    public Collection<ItemDto> searchItem(String query) {
        if (query.isBlank()) return new ArrayList<>();
        return itemRepository.search(query).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toSet());
    }

    @Override
    public CommentDto addComment(Long userId, Long itemId, CommentDto commentDto) {
        User user = checkUser(userId);
        getItemById(itemId);
        List<Booking> itemBooking = bookingRepository.findByItemId(itemId);
        LocalDateTime now = LocalDateTime.now();
        List<Booking> isBooker = itemBooking.stream()
                .filter(b -> b.getBooker().getId().equals(userId))
                .filter(b -> b.getStart().isBefore(now))
                .filter(b -> b.getStatus().equals(BookingStatus.APPROVED))
                .toList();
        if (isBooker.isEmpty()) throw new ConditionsNotMetException("Оставлять комментарии можно только после аренды");
        Comment comment = CommentMapper.toComment(commentDto);
        comment.setAuthorId(userId);
        comment.setAuthorName(user.getName());
        comment.setItemId(itemId);
        comment.setCreated(now);
        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }

    private Item getItemById(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Предмет с ID " + itemId + " не найден"));
    }

    private User checkUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + userId + " не найден"));
    }

    private void checkOwner(Long userId, Item item) {
        if (!Objects.equals(userId, item.getOwner()) || item.getOwner() == null) {
            throw new ConditionsNotMetException("Пользователь с ID " + userId +
                    " не является владельцем предмета с ID " + item.getId());
        }
    }
}
