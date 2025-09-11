package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.ReturnBookingDto;
import ru.practicum.shareit.exceptions.ConditionsNotMetException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public ReturnBookingDto addBooking(Long userId, BookingDto bookingDto) {
        User booker = checkUser(userId);
        Item item = checkItem(bookingDto.getItemId());
        checkTime(bookingDto);
        Booking booking = BookingMapper.toBooking(bookingDto, item, booker);
        bookingRepository.save(booking);
        return BookingMapper.toReturnBookingDto(booking);
    }

    @Override
    public ReturnBookingDto ownerApprove(Long userId, Long bookingId, boolean approve) {
        Booking booking = getBookingById(bookingId);
        Item item = checkItem(booking.getItem().getId());
        checkOwner(userId, item.getOwner());
        if (booking.getEnd().isBefore(LocalDateTime.now()) || booking.getStatus().equals(BookingStatus.CANCELED)) {
            throw new ConditionsNotMetException("Бронирование уже завершено");
        }
        if (booking.getStatus().equals(BookingStatus.WAITING)) {
            if (approve) booking.setStatus(BookingStatus.APPROVED);
            else booking.setStatus(BookingStatus.REJECTED);
        }
        bookingRepository.save(booking);
        return BookingMapper.toReturnBookingDto(booking);
    }

    @Override
    public ReturnBookingDto getBooking(Long userId, Long bookingId) {
        checkUser(userId);
        Booking booking = getBookingById(bookingId);
        Item item = checkItem(booking.getItem().getId());
        if (!(userId.equals(booking.getBooker().getId()) || userId.equals(item.getOwner()))) {
            throw new ConditionsNotMetException("Просматривать бронирование могут только " +
                    "арендатор и владелец бронируемого предмета");
        }
        return BookingMapper.toReturnBookingDto(booking);
    }

    @Override
    public Collection<ReturnBookingDto> getUserBooking(Long userId, String status) {
        checkUser(userId);
        LocalDateTime now = LocalDateTime.now();
        BookingStatus bookingStatus = BookingStatus.valueOf(status.toUpperCase());
        switch (bookingStatus) {
            case BookingStatus.ALL -> {
                return bookingRepository.findByBookerId(userId).stream()
                        .sorted(Comparator.comparing(Booking::getEnd))
                        .map(BookingMapper::toReturnBookingDto)
                        .collect(Collectors.toSet());
            }
            case BookingStatus.WAITING, BookingStatus.REJECTED -> {
                return bookingRepository.findByBookerIdAndStatus(userId, bookingStatus).stream()
                        .sorted(Comparator.comparing(Booking::getEnd))
                        .map(BookingMapper::toReturnBookingDto)
                        .collect(Collectors.toSet());
            }
            case BookingStatus.CURRENT -> {
                return bookingRepository.findByBookerIdAndStartIsBeforeAndEndIsAfter(userId, now, now).stream()
                        .sorted(Comparator.comparing(Booking::getEnd))
                        .map(BookingMapper::toReturnBookingDto)
                        .collect(Collectors.toSet());
            }
            case BookingStatus.PAST -> {
                return bookingRepository.findByBookerIdAndEndIsBefore(userId, now).stream()
                        .sorted(Comparator.comparing(Booking::getEnd))
                        .map(BookingMapper::toReturnBookingDto)
                        .collect(Collectors.toSet());
            }
            case BookingStatus.FUTURE -> {
                return bookingRepository.findByBookerIdAndStartIsAfter(userId, now).stream()
                        .sorted(Comparator.comparing(Booking::getEnd))
                        .map(BookingMapper::toReturnBookingDto)
                        .collect(Collectors.toSet());
            }
            default -> throw new ConditionsNotMetException("Указан некорректный статус (status) бронирования. " +
                    "Параметр status может принимать значения \"all\" (все бронирования; значение по умолчанию), " +
                    "\"waiting\" (бронирования, ожидающие подтверждения от владельца предмета), " +
                    "\"rejected\" (отклоненные бронирования), " +
                    "\"current\" (текущие бронирования), \"past\" (завершившиеся бронирования), " +
                    "\"future\" (будущие бронирования)");
        }
    }

    @Override
    public Collection<ReturnBookingDto> getOwnerBooking(Long userId, String status) {
        checkUser(userId);
        LocalDateTime now = LocalDateTime.now();
        BookingStatus bookingStatus = BookingStatus.valueOf(status);
        switch (bookingStatus) {
            case BookingStatus.ALL -> {
                return bookingRepository.findByItemOwner(userId).stream()
                        .sorted(Comparator.comparing(Booking::getEnd))
                        .map(BookingMapper::toReturnBookingDto)
                        .collect(Collectors.toSet());
            }
            case BookingStatus.WAITING, BookingStatus.REJECTED -> {
                return bookingRepository.findByItemOwnerAndStatus(userId, bookingStatus).stream()
                        .sorted(Comparator.comparing(Booking::getEnd))
                        .map(BookingMapper::toReturnBookingDto)
                        .collect(Collectors.toSet());
            }
            case BookingStatus.CURRENT -> {
                return bookingRepository.findByItemOwnerAndStartIsBeforeAndEndIsAfter(userId, now, now).stream()
                        .sorted(Comparator.comparing(Booking::getEnd))
                        .map(BookingMapper::toReturnBookingDto)
                        .collect(Collectors.toSet());
            }
            case BookingStatus.PAST -> {
                return bookingRepository.findByItemOwnerAndEndIsBefore(userId, now).stream()
                        .sorted(Comparator.comparing(Booking::getEnd))
                        .map(BookingMapper::toReturnBookingDto)
                        .collect(Collectors.toSet());
            }
            case BookingStatus.FUTURE -> {
                return bookingRepository.findByItemOwnerAndStartIsAfter(userId, now).stream()
                        .sorted(Comparator.comparing(Booking::getEnd))
                        .map(BookingMapper::toReturnBookingDto)
                        .collect(Collectors.toSet());
            }
            default -> throw new ConditionsNotMetException("Указан некорректный статус (status) бронирования. " +
                    "Параметр status может принимать значения \"all\" (все бронирования; значение по умолчанию), " +
                    "\"waiting\" (бронирования, ожидающие подтверждения), " +
                    "\"rejected\" (отклоненные бронирования), " +
                    "\"current\" (текущие бронирования), \"past\" (завершившиеся бронирования), " +
                    "\"future\" (будущие бронирования)");
        }
    }

    private User checkUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + userId + " не найден"));
    }

    private Item checkItem(Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Предмет с ID " + itemId + " не найден"));
        if (!item.getAvailable()) {
            throw new ConditionsNotMetException("Предмет с ID " + itemId + " недоступен для аренды");
        }
        return item;
    }

    private Booking getBookingById(Long bookingId) {
        if (bookingId == null) throw new NotFoundException("Бронирование не найдено");
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование с ID " + bookingId + " не найдено"));
    }

    private void checkOwner(Long userId, Long ownerId) {
        if (!userId.equals(ownerId)) {
            throw new ConditionsNotMetException("Подтвердить или отклонить бронирование " +
                    "может только владелец арендуемого предмета");
        }
    }

    private void checkTime(BookingDto booking) {
        LocalDateTime now = LocalDateTime.now();
        if (booking.getEnd().isBefore(booking.getStart())) {
            throw new ConditionsNotMetException("Бронирование не может заканчиваться раньше начала");
        }
        if (booking.getEnd().equals(booking.getStart())) {
            throw new ConditionsNotMetException("Время окончания бронирования не может равняться времени его начала");
        }
        List<Booking> bookingList = bookingRepository.findByItemIdAndStatus(booking.getItemId(), BookingStatus.APPROVED)
                .stream()
                .filter(b -> ((b.getStart().isBefore(booking.getStart()) && b.getEnd().isAfter(booking.getStart()))
                    || (b.getStart().isBefore(booking.getEnd()) && b.getEnd().isAfter(booking.getEnd()))
                    || (b.getStart().isAfter(booking.getStart()) && b.getEnd().isBefore(booking.getEnd()))))
                .toList();
        System.out.println(booking);
        System.out.println(bookingList);
        if (!bookingList.isEmpty()) throw new ConditionsNotMetException("В указанное время предмет уже забронирован");
    }
}
