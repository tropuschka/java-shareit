package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.exceptions.ConditionsNotMetException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Comparator;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    BookingRepository bookingRepository;
    UserRepository userRepository;
    ItemRepository itemRepository;

    @Override
    public BookingDto addBooking(Long userId, BookingDto bookingDto) {
        checkUser(userId);
        Booking booking = BookingMapper.toBooking(bookingDto, checkItem(bookingDto.getItem()));
        booking.setBooker(userId);
        booking.setStatus(BookingStatus.WAITING);
        bookingRepository.save(booking);
        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public BookingDto ownerApprove(Long userId, Long bookingId, boolean approve) {
        checkUser(userId);
        Booking booking = getBookingById(bookingId);
        Item item = checkItem(booking.getItem().getId());
        checkOwner(userId, item.getOwner());
        if (approve) booking.setStatus(BookingStatus.APPROVED);
        else booking.setStatus(BookingStatus.REJECTED);
        bookingRepository.save(booking);
        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public BookingDto getBooking(Long userId, Long bookingId) {
        checkUser(userId);
        Booking booking = getBookingById(bookingId);
        Item item = checkItem(booking.getItem().getId());
        if (!userId.equals(booking.getBooker()) && !userId.equals(item.getOwner())) {
            throw new ConditionsNotMetException("Просматривать бронирование могут только " +
                    "арендатор и владелец бронируемого предмета");
        }
        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public Collection<BookingDto> getUserBooking(Long userId, String status) {
        checkUser(userId);
        status = status.toLowerCase();
        LocalDateTime now = LocalDateTime.now();
        if (status.equals("all")) {
            return bookingRepository.findByBooker(userId).stream()
                    .sorted(Comparator.comparing(Booking::getEnd))
                    .map(BookingMapper::toBookingDto)
                    .collect(Collectors.toSet());
        }
        else if (status.equals("waiting") || status.equals("rejected")) {
            BookingStatus bookingStatus = BookingStatus.valueOf(status);
            return bookingRepository.findByBookerAndStatus(userId, bookingStatus).stream()
                    .sorted(Comparator.comparing(Booking::getEnd))
                    .map(BookingMapper::toBookingDto)
                    .collect(Collectors.toSet());
        } else if (status.equals("current")) {
            return bookingRepository.findByBookerAndStartIsBeforeAndEndIsAfter(userId, now, now).stream()
                    .sorted(Comparator.comparing(Booking::getEnd))
                    .map(BookingMapper::toBookingDto)
                    .collect(Collectors.toSet());
        } else if (status.equals("past")) {
            return bookingRepository.findByBookerAndEndIsBefore(userId, now).stream()
                    .sorted(Comparator.comparing(Booking::getEnd))
                    .map(BookingMapper::toBookingDto)
                    .collect(Collectors.toSet());
        } else if (status.equals("future")) {
            return bookingRepository.findByBookerAndStartIsAfter(userId, now).stream()
                    .sorted(Comparator.comparing(Booking::getEnd))
                    .map(BookingMapper::toBookingDto)
                    .collect(Collectors.toSet());
        } else throw new ConditionsNotMetException("Указан некорректный статус (status) бронирования. " +
                "Параметр status может принимать значения \"all\" (все бронирования; значение по умолчанию), " +
                "\"waiting\" (бронирования, ожидающие подтверждения от владельца предмета), " +
                "\"rejected\" (отклоненные бронирования), " +
                "\"current\" (текущие бронирования), \"past\" (завершившиеся бронирования), " +
                "\"future\" (будущие бронирования)");
    }

    @Override
    public Collection<BookingDto> getOwnerBooking(Long userId, String status) {
        checkUser(userId);
        status = status.toLowerCase();
        LocalDateTime now = LocalDateTime.now();
        if (status.equals("all")) {
            return bookingRepository.findByItemOwner(userId).stream()
                    .sorted(Comparator.comparing(Booking::getEnd))
                    .map(BookingMapper::toBookingDto)
                    .collect(Collectors.toSet());
        }
        else if (status.equals("waiting") || status.equals("rejected")) {
            BookingStatus bookingStatus = BookingStatus.valueOf(status);
            return bookingRepository.findByItemOwnerAndStatus(userId, bookingStatus).stream()
                    .sorted(Comparator.comparing(Booking::getEnd))
                    .map(BookingMapper::toBookingDto)
                    .collect(Collectors.toSet());
        } else if (status.equals("current")) {
            return bookingRepository.findByItemOwnerAndStartIsBeforeAndEndIsAfter(userId, now, now).stream()
                    .sorted(Comparator.comparing(Booking::getEnd))
                    .map(BookingMapper::toBookingDto)
                    .collect(Collectors.toSet());
        } else if (status.equals("past")) {
            return bookingRepository.findByItemOwnerAndEndIsBefore(userId, now).stream()
                    .sorted(Comparator.comparing(Booking::getEnd))
                    .map(BookingMapper::toBookingDto)
                    .collect(Collectors.toSet());
        } else if (status.equals("future")) {
            return bookingRepository.findByItemOwnerAndStartIsAfter(userId, now).stream()
                    .sorted(Comparator.comparing(Booking::getEnd))
                    .map(BookingMapper::toBookingDto)
                    .collect(Collectors.toSet());
        } else throw new ConditionsNotMetException("Указан некорректный статус (status) бронирования. " +
                "Параметр status может принимать значения \"all\" (все бронирования; значение по умолчанию), " +
                "\"waiting\" (бронирования, ожидающие подтверждения), " +
                "\"rejected\" (отклоненные бронирования), " +
                "\"current\" (текущие бронирования), \"past\" (завершившиеся бронирования), " +
                "\"future\" (будущие бронирования)");
    }

    private void checkUser(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + userId + " не найден"));
    }

    private Item checkItem(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Предмет с ID " + itemId + " не найден"));
    }

    private Booking getBookingById(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование с ID " + bookingId + " не найдено"));
    }

    private void checkOwner(Long userId, Long ownerId) {
        if (!userId.equals(ownerId)) {
            throw new ConditionsNotMetException("Подтвердить или отклонить бронирование " +
                    "может только владелец арендуемого предмета");
        }
    }
}
