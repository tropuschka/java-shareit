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
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public BookingDto addBooking(Long userId, BookingDto bookingDto) {
        checkUser(userId);
        checkTime(bookingDto);
        Item item = checkItem(bookingDto.getItem());
        Booking booking = BookingMapper.toBooking(bookingDto, item);
        booking.setBooker(userId);
        booking.setStatus(BookingStatus.WAITING);
        bookingRepository.save(booking);
        System.out.println(booking);
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
        if (userId == null) throw new NotFoundException("Пользователь не найден");
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + userId + " не найден"));
    }

    private Item checkItem(Long itemId) {
//        if (itemId == null) throw new NotFoundException("Предмет  не найден");
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
        if (booking.getEnd().isBefore(now)) {
            throw new ConditionsNotMetException("Дата окончания бронирования не может находиться в прошлом");
        }
        if (booking.getEnd().isBefore(booking.getStart())) {
            throw new ConditionsNotMetException("Бронирование не может заканчиваться раньше начала");
        }
        if (booking.getEnd().equals(booking.getStart())) {
            throw new ConditionsNotMetException("Время окончания бронирования не может равняться времени его начала");
        }
        if (booking.getStart().isBefore(now)) {
            throw new ConditionsNotMetException("Дата начала бронирования не может находиться в прошлом");
        }
    }
}
