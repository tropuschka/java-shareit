package practicum.ru.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import practicum.ru.shareit.booking.dto.BookingDto;
import practicum.ru.shareit.booking.dto.BookingMapper;
import practicum.ru.shareit.booking.dto.ReturnBookingDto;
import practicum.ru.shareit.exceptions.ConditionsNotMetException;
import practicum.ru.shareit.exceptions.NotFoundException;
import practicum.ru.shareit.item.Item;
import practicum.ru.shareit.item.ItemRepository;
import practicum.ru.shareit.user.User;
import practicum.ru.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
        checkItemAvailable(item);
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
        BookingState bookingState;
        try {
            bookingState = BookingState.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            return new ArrayList<>();
        }
        switch (bookingState) {
            case BookingState.ALL -> {
                return bookingRepository.findByBookerId(userId).stream()
                        .sorted(Comparator.comparing(Booking::getEnd))
                        .map(BookingMapper::toReturnBookingDto)
                        .collect(Collectors.toSet());
            }
            case BookingState.WAITING, BookingState.REJECTED -> {
                BookingStatus bookingStatus = BookingStatus.valueOf(bookingState.toString());
                return bookingRepository.findByBookerIdAndStatus(userId, bookingStatus).stream()
                        .sorted(Comparator.comparing(Booking::getEnd))
                        .map(BookingMapper::toReturnBookingDto)
                        .collect(Collectors.toSet());
            }
            case BookingState.CURRENT -> {
                return bookingRepository
                        .findByBookerIdAndStartIsBeforeAndEndIsAfterAndStatus(userId, now, now, BookingStatus.APPROVED)
                        .stream()
                        .sorted(Comparator.comparing(Booking::getEnd))
                        .map(BookingMapper::toReturnBookingDto)
                        .collect(Collectors.toSet());
            }
            case BookingState.PAST -> {
                return bookingRepository.findByBookerIdAndEndIsBefore(userId, now).stream()
                        .sorted(Comparator.comparing(Booking::getEnd))
                        .map(BookingMapper::toReturnBookingDto)
                        .collect(Collectors.toSet());
            }
            case BookingState.FUTURE -> {
                return bookingRepository.findByBookerIdAndStartIsAfter(userId, now).stream()
                        .sorted(Comparator.comparing(Booking::getEnd))
                        .map(BookingMapper::toReturnBookingDto)
                        .collect(Collectors.toSet());
            }
            default -> {
                return new ArrayList<>();
            }
        }
    }

    @Override
    public Collection<ReturnBookingDto> getOwnerBooking(Long userId, String status) {
        checkUser(userId);
        LocalDateTime now = LocalDateTime.now();
        BookingState bookingState;
        try {
            bookingState = BookingState.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            return new ArrayList<>();
        }
        switch (bookingState) {
            case BookingState.ALL -> {
                return bookingRepository.findByItemOwner(userId).stream()
                        .sorted(Comparator.comparing(Booking::getEnd))
                        .map(BookingMapper::toReturnBookingDto)
                        .collect(Collectors.toSet());
            }
            case BookingState.WAITING, BookingState.REJECTED -> {
                BookingStatus bookingStatus = BookingStatus.valueOf(bookingState.toString());
                return bookingRepository.findByItemOwnerAndStatus(userId, bookingStatus).stream()
                        .sorted(Comparator.comparing(Booking::getEnd))
                        .map(BookingMapper::toReturnBookingDto)
                        .collect(Collectors.toSet());
            }
            case BookingState.CURRENT -> {
                return bookingRepository
                        .findByItemOwnerAndStartIsBeforeAndEndIsAfterAndStatus(userId, now, now, BookingStatus.APPROVED)
                        .stream()
                        .sorted(Comparator.comparing(Booking::getEnd))
                        .map(BookingMapper::toReturnBookingDto)
                        .collect(Collectors.toSet());
            }
            case BookingState.PAST -> {
                return bookingRepository.findByItemOwnerAndEndIsBefore(userId, now).stream()
                        .sorted(Comparator.comparing(Booking::getEnd))
                        .map(BookingMapper::toReturnBookingDto)
                        .collect(Collectors.toSet());
            }
            case BookingState.FUTURE -> {
                return bookingRepository.findByItemOwnerAndStartIsAfter(userId, now).stream()
                        .sorted(Comparator.comparing(Booking::getEnd))
                        .map(BookingMapper::toReturnBookingDto)
                        .collect(Collectors.toSet());
            }
            default -> {
                return new ArrayList<>();
            }
        }
    }

    private User checkUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + userId + " не найден"));
    }

    private Item checkItem(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Предмет с ID " + itemId + " не найден"));
    }

    private void checkItemAvailable(Item item) {
        if (!item.getAvailable()) {
            throw new ConditionsNotMetException("Предмет с ID " + item.getId() + " недоступен для аренды");
        }
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
                .filter(b -> (b.getStart().isBefore(booking.getEnd()) && booking.getStart().isBefore(b.getEnd())))
                .toList();
        System.out.println(booking);
        System.out.println(bookingList);
        if (!bookingList.isEmpty()) throw new ConditionsNotMetException("В указанное время предмет уже забронирован");
    }
}
