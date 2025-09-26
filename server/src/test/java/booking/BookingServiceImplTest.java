package booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import practicum.ru.shareit.ShareItServer;
import practicum.ru.shareit.booking.*;
import practicum.ru.shareit.booking.dto.BookingDto;
import practicum.ru.shareit.booking.dto.BookingMapper;
import practicum.ru.shareit.booking.dto.ReturnBookingDto;
import practicum.ru.shareit.exceptions.ConditionsNotMetException;
import practicum.ru.shareit.exceptions.NotFoundException;
import practicum.ru.shareit.item.Item;
import practicum.ru.shareit.user.User;

import java.time.LocalDateTime;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@ContextConfiguration(classes = ShareItServer.class)
@Import({BookingServiceImpl.class})
@ActiveProfiles("test")
public class BookingServiceImplTest {
    @Autowired
    BookingServiceImpl bookingService;
    @Autowired
    BookingRepository bookingRepository;
    @Autowired
    TestEntityManager entityManager;
    private Item savedItem;
    private User itemOwner;
    private User booker;
    private User foreignUser;
    private Booking futureBooking;
    private Booking waitingBooking;
    private Booking rejectedBooking;
    private Booking currentBooking;
    private Booking oldBooking;
    private Booking foreignBooking;

    @BeforeEach
    void setUp() {
        bookingRepository.deleteAll();
        entityManager.flush();
        entityManager.clear();

        itemOwner = new User();
        itemOwner.setName("Gazbiyya");
        itemOwner.setEmail("gazbiyya_reyes@mail.com");
        itemOwner = entityManager.persistAndFlush(itemOwner);
        booker = new User();
        booker.setName("Jaclyn");
        booker.setEmail("jaclyn_bravo@mail.com");
        booker = entityManager.persistAndFlush(booker);
        foreignUser = new User();
        foreignUser.setName("Kennet");
        foreignUser.setEmail("kennet_longo@mail.com");
        foreignUser = entityManager.persistAndFlush(foreignUser);

        savedItem = new Item();
        savedItem.setName("Mirror");
        savedItem.setDescription("A small mirror");
        savedItem.setOwner(itemOwner.getId());
        savedItem.setAvailable(true);
        savedItem = entityManager.persistAndFlush(savedItem);

        futureBooking = new Booking();
        futureBooking.setItem(savedItem);
        futureBooking.setBooker(booker);
        futureBooking.setStart(LocalDateTime.now().plusHours(1));
        futureBooking.setEnd(LocalDateTime.now().plusHours(3));
        futureBooking.setStatus(BookingStatus.APPROVED);
        futureBooking = entityManager.persistAndFlush(futureBooking);

        waitingBooking = new Booking();
        waitingBooking.setItem(savedItem);
        waitingBooking.setBooker(booker);
        waitingBooking.setStart(LocalDateTime.now().plusYears(1));
        waitingBooking.setEnd(LocalDateTime.now().plusYears(3));
        waitingBooking.setStatus(BookingStatus.WAITING);
        waitingBooking = entityManager.persistAndFlush(waitingBooking);

        rejectedBooking = new Booking();
        rejectedBooking.setItem(savedItem);
        rejectedBooking.setBooker(booker);
        rejectedBooking.setStart(LocalDateTime.now().minusMinutes(2));
        rejectedBooking.setEnd(LocalDateTime.now().plusHours(2));
        rejectedBooking.setStatus(BookingStatus.REJECTED);
        rejectedBooking = entityManager.persistAndFlush(rejectedBooking);

        currentBooking = new Booking();
        currentBooking.setItem(savedItem);
        currentBooking.setBooker(booker);
        currentBooking.setStart(LocalDateTime.now().minusHours(1));
        currentBooking.setEnd(LocalDateTime.now().plusMinutes(16));
        currentBooking.setStatus(BookingStatus.APPROVED);
        currentBooking = entityManager.persistAndFlush(currentBooking);

        oldBooking = new Booking();
        oldBooking.setItem(savedItem);
        oldBooking.setBooker(booker);
        oldBooking.setStart(LocalDateTime.now().minusYears(3));
        oldBooking.setEnd(LocalDateTime.now().minusYears(2));
        oldBooking.setStatus(BookingStatus.APPROVED);
        oldBooking = entityManager.persistAndFlush(oldBooking);

        foreignBooking = new Booking();
        foreignBooking.setItem(savedItem);
        foreignBooking.setBooker(foreignUser);
        foreignBooking.setStart(LocalDateTime.now().plusMonths(1));
        foreignBooking.setEnd(LocalDateTime.now().plusMonths(3));
        foreignBooking.setStatus(BookingStatus.APPROVED);
        foreignBooking = entityManager.persistAndFlush(foreignBooking);
    }

    @Test
    void addBooking() {
        LocalDateTime start = LocalDateTime.now().plusHours(5);
        LocalDateTime end = LocalDateTime.now().plusHours(10);
        BookingDto bookingDto = new BookingDto(null, savedItem.getId(), booker.getId(), start, end, null);
        ReturnBookingDto savedBookingDto = bookingService.addBooking(booker.getId(), bookingDto);

        assertThat(savedBookingDto.getId()).isNotNull();
        assertThat(savedBookingDto.getItem()).isEqualTo(savedItem);
        assertThat(savedBookingDto.getBooker()).isEqualTo(booker);
        assertThat(savedBookingDto.getStart()).isEqualTo(start);
        assertThat(savedBookingDto.getEnd()).isEqualTo(end);
        assertThat(savedBookingDto.getStatus()).isEqualTo(BookingStatus.WAITING);

        Booking savedBooking = entityManager.find(Booking.class, savedBookingDto.getId());
        assertThat(savedBooking).isNotNull();
        assertThat(savedBooking.getItem()).isEqualTo(savedItem);
        assertThat(savedBooking.getBooker()).isEqualTo(booker);
        assertThat(savedBooking.getStart()).isEqualTo(start);
        assertThat(savedBooking.getEnd()).isEqualTo(end);
        assertThat(savedBooking.getStatus()).isEqualTo(BookingStatus.WAITING);
    }

    @Test
    void addBookingNotExistingUser() {
        entityManager.remove(booker);
        LocalDateTime start = LocalDateTime.now().plusHours(5);
        LocalDateTime end = LocalDateTime.now().plusHours(10);
        BookingDto bookingDto = new BookingDto(null, savedItem.getId(), booker.getId(), start, end, null);

        final NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingService.addBooking(booker.getId(), bookingDto));
        assertEquals("Пользователь с ID " + booker.getId() + " не найден", exception.getMessage());
    }

    @Test
    void addBookingNotExistingItem() {
        entityManager.remove(savedItem);
        LocalDateTime start = LocalDateTime.now().plusHours(5);
        LocalDateTime end = LocalDateTime.now().plusHours(10);
        BookingDto bookingDto = new BookingDto(null, savedItem.getId(), booker.getId(), start, end, null);

        final NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingService.addBooking(booker.getId(), bookingDto));
        assertEquals("Предмет с ID " + savedItem.getId() + " не найден", exception.getMessage());
    }

    @Test
    void addBookingUnavailableItem() {
        savedItem.setAvailable(false);
        savedItem = entityManager.persistAndFlush(savedItem);
        LocalDateTime start = LocalDateTime.now().plusHours(5);
        LocalDateTime end = LocalDateTime.now().plusHours(10);
        BookingDto bookingDto = new BookingDto(null, savedItem.getId(), booker.getId(), start, end, null);

        final ConditionsNotMetException exception = assertThrows(ConditionsNotMetException.class,
                () -> bookingService.addBooking(booker.getId(), bookingDto));
        assertEquals("Предмет с ID " + savedItem.getId() + " недоступен для аренды", exception.getMessage());
    }

    @Test
    void addBookingEndBeforeStart() {
        LocalDateTime start = LocalDateTime.now().plusHours(10);
        LocalDateTime end = LocalDateTime.now().plusHours(5);
        BookingDto bookingDto = new BookingDto(null, savedItem.getId(), booker.getId(), start, end, null);

        final ConditionsNotMetException exception = assertThrows(ConditionsNotMetException.class,
                () -> bookingService.addBooking(booker.getId(), bookingDto));
        assertEquals("Бронирование не может заканчиваться раньше начала", exception.getMessage());
    }

    @Test
    void addBookingOverlap() {
        LocalDateTime start = LocalDateTime.now().plusHours(1);
        LocalDateTime end = LocalDateTime.now().plusHours(2);
        BookingDto bookingDto = new BookingDto(null, savedItem.getId(), booker.getId(), start, end, null);
        entityManager.persistAndFlush(futureBooking);

        final ConditionsNotMetException exception = assertThrows(ConditionsNotMetException.class,
                () -> bookingService.addBooking(booker.getId(), bookingDto));
        assertEquals("В указанное время предмет уже забронирован", exception.getMessage());
    }

    @Test
    void addBookingEndEqualsStart() {
        LocalDateTime start = LocalDateTime.now().plusHours(1);
        BookingDto bookingDto = new BookingDto(null, savedItem.getId(), booker.getId(), start, start, null);

        final ConditionsNotMetException exception = assertThrows(ConditionsNotMetException.class,
                () -> bookingService.addBooking(booker.getId(), bookingDto));
        assertEquals("Время окончания бронирования не может равняться времени его начала", exception.getMessage());
    }

    @Test
    void ownerApproveTrue() {
        ReturnBookingDto approvedBookingDto = bookingService
                .ownerApprove(itemOwner.getId(), waitingBooking.getId(), true);
        assertThat(approvedBookingDto.getStatus()).isEqualTo(BookingStatus.APPROVED);
        assertThat(waitingBooking.getStatus()).isEqualTo(BookingStatus.APPROVED);
    }

    @Test
    void ownerApproveFalse() {
        ReturnBookingDto approvedBookingDto = bookingService
                .ownerApprove(itemOwner.getId(), waitingBooking.getId(), false);
        assertThat(approvedBookingDto.getStatus()).isEqualTo(BookingStatus.REJECTED);
        assertThat(waitingBooking.getStatus()).isEqualTo(BookingStatus.REJECTED);
    }

    @Test
    void ownerApproveNullBooking() {
        final NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingService.ownerApprove(itemOwner.getId(), null, true));
        assertEquals("Бронирование не найдено", exception.getMessage());
    }

    @Test
    void ownerApproveNotExistingBooking() {
        bookingRepository.delete(waitingBooking);
        final NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingService.ownerApprove(itemOwner.getId(), waitingBooking.getId(), true));
        assertEquals("Бронирование с ID " + waitingBooking.getId() + " не найдено", exception.getMessage());
    }

    @Test
    void ownerApproveNotExistingItem() {
        entityManager.remove(savedItem);
        final NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingService.ownerApprove(itemOwner.getId(), waitingBooking.getId(), true));
        assertEquals("Предмет с ID " + savedItem.getId() + " не найден", exception.getMessage());
    }

    @Test
    void ownerApproveCancelledBooking() {
        waitingBooking.setStatus(BookingStatus.CANCELED);
        Booking savedBooking = entityManager.persistAndFlush(waitingBooking);

        final ConditionsNotMetException exception = assertThrows(ConditionsNotMetException.class,
                () -> bookingService.ownerApprove(itemOwner.getId(), savedBooking.getId(), true));
        assertEquals("Бронирование уже завершено", exception.getMessage());
    }

    @Test
    void getBookingOwner() {
        ReturnBookingDto foundBookingDto = bookingService.getBooking(itemOwner.getId(), waitingBooking.getId());
        assertThat(foundBookingDto).isNotNull();
        assertThat(foundBookingDto.getItem()).isEqualTo(savedItem);
        assertThat(foundBookingDto.getBooker()).isEqualTo(booker);
        assertThat(foundBookingDto.getStart()).isEqualTo(waitingBooking.getStart());
        assertThat(foundBookingDto.getEnd()).isEqualTo(waitingBooking.getEnd());
        assertThat(foundBookingDto.getStatus()).isEqualTo(BookingStatus.WAITING);
    }

    @Test
    void getBookingBooker() {
        ReturnBookingDto foundBookingDto = bookingService.getBooking(booker.getId(), waitingBooking.getId());
        assertThat(foundBookingDto).isNotNull();
        assertThat(foundBookingDto.getItem()).isEqualTo(savedItem);
        assertThat(foundBookingDto.getBooker()).isEqualTo(booker);
        assertThat(foundBookingDto.getStart()).isEqualTo(waitingBooking.getStart());
        assertThat(foundBookingDto.getEnd()).isEqualTo(waitingBooking.getEnd());
        assertThat(foundBookingDto.getStatus()).isEqualTo(BookingStatus.WAITING);
    }

    @Test
    void getBookingNotExistingUser() {
        entityManager.remove(booker);
        final NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingService.getBooking(booker.getId(), waitingBooking.getId()));
        assertEquals("Пользователь с ID " + booker.getId() + " не найден", exception.getMessage());
    }

    @Test
    void getNullBooking() {
        final NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingService.getBooking(booker.getId(), null));
        assertEquals("Бронирование не найдено", exception.getMessage());
    }

    @Test
    void getNotExistingBooking() {
        bookingRepository.delete(waitingBooking);

        final NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingService.getBooking(booker.getId(), waitingBooking.getId()));
        assertEquals("Бронирование с ID " + waitingBooking.getId() + " не найдено", exception.getMessage());
    }

    @Test
    void getBookingNotExistingItem() {
        entityManager.remove(savedItem);
        final NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingService.getBooking(booker.getId(), waitingBooking.getId()));
        assertEquals("Предмет с ID " + savedItem.getId() + " не найден", exception.getMessage());
    }

    @Test
    void getBookingForeignUser() {
        final ConditionsNotMetException exception = assertThrows(ConditionsNotMetException.class,
                () -> bookingService.getBooking(foreignUser.getId(), waitingBooking.getId()));
        assertEquals("Просматривать бронирование могут только " +
                "арендатор и владелец бронируемого предмета", exception.getMessage());
    }

    @Test
    void getUserBookingAll() {
        Collection<ReturnBookingDto> userBookingList = bookingService.getUserBooking(booker.getId(), "all");
        assertThat(userBookingList.size()).isEqualTo(5);
        assertThat(userBookingList.contains(BookingMapper.toReturnBookingDto(currentBooking))).isTrue();
        assertThat(userBookingList.contains(BookingMapper.toReturnBookingDto(foreignBooking))).isFalse();
    }

    @Test
    void getNotExistingUserBooking() {
        entityManager.remove(booker);
        final NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingService.getUserBooking(booker.getId(), "all"));
        assertEquals("Пользователь с ID " + booker.getId() + " не найден", exception.getMessage());
    }

    @Test
    void getUserBookingWaiting() {
        Collection<ReturnBookingDto> userBookingList = bookingService.getUserBooking(booker.getId(), "waiting");
        assertThat(userBookingList.size()).isEqualTo(1);
        assertThat(userBookingList.contains(BookingMapper.toReturnBookingDto(waitingBooking))).isTrue();
        assertThat(userBookingList.contains(BookingMapper.toReturnBookingDto(foreignBooking))).isFalse();
        assertThat(userBookingList.contains(BookingMapper.toReturnBookingDto(currentBooking))).isFalse();
    }

    @Test
    void getUserBookingRejected() {
        Collection<ReturnBookingDto> userBookingList = bookingService.getUserBooking(booker.getId(), "rejected");
        assertThat(userBookingList.size()).isEqualTo(1);
        assertThat(userBookingList.contains(BookingMapper.toReturnBookingDto(rejectedBooking))).isTrue();
        assertThat(userBookingList.contains(BookingMapper.toReturnBookingDto(foreignBooking))).isFalse();
        assertThat(userBookingList.contains(BookingMapper.toReturnBookingDto(currentBooking))).isFalse();
    }

    @Test
    void getUserBookingCurrent() {
        Collection<ReturnBookingDto> userBookingList = bookingService.getUserBooking(booker.getId(), "current");
        assertThat(userBookingList.size()).isEqualTo(1);
        assertThat(userBookingList.contains(BookingMapper.toReturnBookingDto(currentBooking))).isTrue();
        assertThat(userBookingList.contains(BookingMapper.toReturnBookingDto(foreignBooking))).isFalse();
        assertThat(userBookingList.contains(BookingMapper.toReturnBookingDto(futureBooking))).isFalse();
    }

    @Test
    void getUserBookingPast() {
        Collection<ReturnBookingDto> userBookingList = bookingService.getUserBooking(booker.getId(), "past");
        assertThat(userBookingList.size()).isEqualTo(1);
        assertThat(userBookingList.contains(BookingMapper.toReturnBookingDto(oldBooking))).isTrue();
        assertThat(userBookingList.contains(BookingMapper.toReturnBookingDto(foreignBooking))).isFalse();
        assertThat(userBookingList.contains(BookingMapper.toReturnBookingDto(futureBooking))).isFalse();
    }

    @Test
    void getUserBookingFuture() {
        Collection<ReturnBookingDto> userBookingList = bookingService.getUserBooking(booker.getId(), "future");
        assertThat(userBookingList.size()).isEqualTo(2);
        assertThat(userBookingList.contains(BookingMapper.toReturnBookingDto(futureBooking))).isTrue();
        assertThat(userBookingList.contains(BookingMapper.toReturnBookingDto(foreignBooking))).isFalse();
        assertThat(userBookingList.contains(BookingMapper.toReturnBookingDto(currentBooking))).isFalse();
    }

    @Test
    void getUserBookingDefault() {
        Collection<ReturnBookingDto> userBookingList = bookingService.getUserBooking(booker.getId(), "mew");
        assertThat(userBookingList.size()).isEqualTo(0);
    }

    @Test
    void getUserBookingEmpty() {
        bookingRepository.deleteAll();
        Collection<ReturnBookingDto> userBookingList = bookingService.getUserBooking(booker.getId(), "all");
        assertThat(userBookingList.size()).isEqualTo(0);
    }

    @Test
    void getOwnerBookingAll() {
        Collection<ReturnBookingDto> userBookingList = bookingService.getOwnerBooking(itemOwner.getId(), "all");
        assertThat(userBookingList.size()).isEqualTo(6);
        assertThat(userBookingList.contains(BookingMapper.toReturnBookingDto(currentBooking))).isTrue();
        assertThat(userBookingList.contains(BookingMapper.toReturnBookingDto(foreignBooking))).isTrue();
    }

    @Test
    void getNotExistingOwnerBooking() {
        entityManager.remove(itemOwner);
        final NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingService.getOwnerBooking(itemOwner.getId(), "all"));
        assertEquals("Пользователь с ID " + itemOwner.getId() + " не найден", exception.getMessage());
    }

    @Test
    void getOwnerBookingWaiting() {
        Collection<ReturnBookingDto> userBookingList = bookingService.getOwnerBooking(itemOwner.getId(), "waiting");
        assertThat(userBookingList.size()).isEqualTo(1);
        assertThat(userBookingList.contains(BookingMapper.toReturnBookingDto(waitingBooking))).isTrue();
        assertThat(userBookingList.contains(BookingMapper.toReturnBookingDto(currentBooking))).isFalse();
    }

    @Test
    void getOwnerBookingRejected() {
        Collection<ReturnBookingDto> userBookingList = bookingService.getOwnerBooking(itemOwner.getId(), "rejected");
        assertThat(userBookingList.size()).isEqualTo(1);
        assertThat(userBookingList.contains(BookingMapper.toReturnBookingDto(rejectedBooking))).isTrue();
        assertThat(userBookingList.contains(BookingMapper.toReturnBookingDto(currentBooking))).isFalse();
    }

    @Test
    void getOwnerBookingCurrent() {
        Collection<ReturnBookingDto> userBookingList = bookingService.getOwnerBooking(itemOwner.getId(), "current");
        assertThat(userBookingList.size()).isEqualTo(1);
        assertThat(userBookingList.contains(BookingMapper.toReturnBookingDto(currentBooking))).isTrue();
        assertThat(userBookingList.contains(BookingMapper.toReturnBookingDto(futureBooking))).isFalse();
    }

    @Test
    void getOwnerBookingPast() {
        Collection<ReturnBookingDto> userBookingList = bookingService.getOwnerBooking(itemOwner.getId(), "past");
        assertThat(userBookingList.size()).isEqualTo(1);
        assertThat(userBookingList.contains(BookingMapper.toReturnBookingDto(oldBooking))).isTrue();
        assertThat(userBookingList.contains(BookingMapper.toReturnBookingDto(futureBooking))).isFalse();
    }

    @Test
    void getOwnerBookingFuture() {
        Collection<ReturnBookingDto> userBookingList = bookingService.getOwnerBooking(itemOwner.getId(), "future");
        assertThat(userBookingList.size()).isEqualTo(3);
        assertThat(userBookingList.contains(BookingMapper.toReturnBookingDto(futureBooking))).isTrue();
        assertThat(userBookingList.contains(BookingMapper.toReturnBookingDto(currentBooking))).isFalse();
    }

    @Test
    void getOwnerBookingDefault() {
        Collection<ReturnBookingDto> userBookingList = bookingService.getOwnerBooking(itemOwner.getId(), "mew");
        assertThat(userBookingList.size()).isEqualTo(0);
    }

    @Test
    void getOwnerBookingEmpty() {
        bookingRepository.deleteAll();
        Collection<ReturnBookingDto> userBookingList = bookingService.getOwnerBooking(itemOwner.getId(), "all");
        assertThat(userBookingList.size()).isEqualTo(0);
    }
}
