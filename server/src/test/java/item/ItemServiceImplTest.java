package item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import practicum.ru.shareit.ShareItServer;
import practicum.ru.shareit.booking.Booking;
import practicum.ru.shareit.booking.BookingStatus;
import practicum.ru.shareit.exceptions.ConditionsNotMetException;
import practicum.ru.shareit.exceptions.NotFoundException;
import practicum.ru.shareit.item.*;
import practicum.ru.shareit.item.dto.CommentDto;
import practicum.ru.shareit.item.dto.ItemDto;
import practicum.ru.shareit.item.dto.ItemDtoWithBooking;
import practicum.ru.shareit.item.dto.ItemMapper;
import practicum.ru.shareit.request.Request;
import practicum.ru.shareit.user.User;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@ContextConfiguration(classes = ShareItServer.class)
@Import({ItemServiceImpl.class})
@ActiveProfiles("test")
public class ItemServiceImplTest {
    @Autowired
    private ItemServiceImpl itemService;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private TestEntityManager entityManager;
    private User savedUser;
    private User savedUser2;
    private Item item;
    private Booking booking;
    private Request request;
    private Comment comment;

    @BeforeEach
    void setUp() {
        itemRepository.deleteAll();
        commentRepository.deleteAll();
        entityManager.flush();
        entityManager.clear();

        savedUser = new User();
        savedUser.setName("Mao");
        savedUser.setEmail("mao_mateos@mail.com");
        savedUser = entityManager.persistAndFlush(savedUser);

        savedUser2 = new User();
        savedUser2.setName("Clothilde");
        savedUser2.setEmail("clothilde_lindberg@mail.com");
        savedUser2 = entityManager.persistAndFlush(savedUser2);

        item = new Item();
        item.setName("Woman Hat");
        item.setDescription("Blue hat with laces");
        item.setAvailable(true);
        item.setOwner(savedUser.getId());
        item = entityManager.persistAndFlush(item);

        booking = new Booking();
        booking.setItem(entityManager.find(Item.class, item.getId()));
        booking.setBooker(savedUser2);
        booking.setStart(LocalDateTime.now().minusHours(2));
        booking.setEnd(LocalDateTime.now().minusHours(1));
        booking.setStatus(BookingStatus.APPROVED);
        booking = entityManager.persistAndFlush(booking);

        request = new Request();
        request.setRequestor(savedUser2);
        request.setDescription("Woman hat");
        request.setCreated(LocalDateTime.now());
        request = entityManager.persistAndFlush(request);

        comment = new Comment();
        comment.setText("Text");
        comment.setItemId(item.getId());
        comment.setAuthorId(savedUser2.getId());
        comment.setAuthorName(savedUser2.getName());
        comment.setCreated(LocalDateTime.now());
        comment = entityManager.persistAndFlush(comment);
    }

    @Test
    void addItem() {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Fork");
        itemDto.setDescription("Silver fork");
        itemDto.setAvailable(true);
        itemDto.setRequestId(request.getId());
        ItemDto createdItemDto = itemService.addItem(savedUser.getId(), itemDto);

        assertThat(createdItemDto.getId()).isNotNull();
        assertThat(createdItemDto.getName()).isEqualTo("Fork");
        assertThat(createdItemDto.getDescription()).isEqualTo("Silver fork");
        assertThat(createdItemDto.getAvailable()).isTrue();

        Item savedItem = entityManager.find(Item.class, createdItemDto.getId());
        assertThat(savedItem).isNotNull();
        assertThat(savedItem.getName()).isEqualTo("Fork");
        assertThat(savedItem.getDescription()).isEqualTo("Silver fork");
        assertThat(savedItem.getAvailable()).isTrue();
        assertThat(savedItem.getOwner()).isEqualTo(savedUser.getId());
    }

    @Test
    void addItemNoRequest() {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Fork");
        itemDto.setDescription("Silver fork");
        itemDto.setAvailable(true);
        ItemDto createdItemDto = itemService.addItem(savedUser.getId(), itemDto);

        assertThat(createdItemDto.getId()).isNotNull();
        assertThat(createdItemDto.getName()).isEqualTo("Fork");
        assertThat(createdItemDto.getDescription()).isEqualTo("Silver fork");
        assertThat(createdItemDto.getAvailable()).isTrue();

        Item savedItem = entityManager.find(Item.class, createdItemDto.getId());
        assertThat(savedItem).isNotNull();
        assertThat(savedItem.getName()).isEqualTo("Fork");
        assertThat(savedItem.getDescription()).isEqualTo("Silver fork");
        assertThat(savedItem.getAvailable()).isTrue();
        assertThat(savedItem.getOwner()).isEqualTo(savedUser.getId());
    }

    @Test
    void addItemNotExistingUser() {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Fork");
        itemDto.setDescription("Silver fork");
        itemDto.setAvailable(true);
        entityManager.remove(savedUser);
        final NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemService.addItem(savedUser.getId(), itemDto));
        assertEquals("Пользователь с ID " + savedUser.getId() + " не найден", exception.getMessage());
    }

    @Test
    void addItemNotExistingRequest() {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Fork");
        itemDto.setDescription("Silver fork");
        itemDto.setAvailable(true);
        itemDto.setRequestId(request.getId());
        entityManager.remove(request);
        final NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemService.addItem(savedUser.getId(), itemDto));
        assertEquals("Запрос с ID " + request.getId() + " не найден", exception.getMessage());
    }

    @Test
    void updateItem() {
        ItemDto newItemDto = new ItemDto();
        newItemDto.setName("Man Hat");
        newItemDto.setDescription("Red hat with laces");
        newItemDto.setAvailable(false);
        newItemDto.setRequestId(request.getId());

        ItemDto updatedItemDto = itemService.updateItem(savedUser.getId(), item.getId(), newItemDto);
        assertThat(updatedItemDto.getId()).isNotNull();
        assertThat(updatedItemDto.getName()).isEqualTo("Man Hat");
        assertThat(updatedItemDto.getDescription()).isEqualTo("Red hat with laces");
        assertThat(updatedItemDto.getAvailable()).isFalse();

        Item updatedItem = entityManager.find(Item.class, item.getId());
        assertThat(updatedItem).isNotNull();
        assertThat(updatedItem.getName()).isEqualTo("Man Hat");
        assertThat(updatedItem.getDescription()).isEqualTo("Red hat with laces");
        assertThat(updatedItem.getAvailable()).isFalse();
    }

    @Test
    void updateItemName() {
        ItemDto newItemDto = new ItemDto();
        newItemDto.setName("Man Hat");

        ItemDto updatedItemDto = itemService.updateItem(savedUser.getId(), item.getId(), newItemDto);
        assertThat(updatedItemDto.getId()).isNotNull();
        assertThat(updatedItemDto.getName()).isEqualTo("Man Hat");
        assertThat(updatedItemDto.getDescription()).isEqualTo("Blue hat with laces");
        assertThat(updatedItemDto.getAvailable()).isTrue();

        Item updatedItem = entityManager.find(Item.class, item.getId());
        assertThat(updatedItem).isNotNull();
        assertThat(updatedItem.getName()).isEqualTo("Man Hat");
        assertThat(updatedItem.getDescription()).isEqualTo("Blue hat with laces");
        assertThat(updatedItem.getAvailable()).isTrue();
    }

    @Test
    void updateItemDescription() {
        ItemDto newItemDto = new ItemDto();
        newItemDto.setDescription("Red hat with laces");

        ItemDto updatedItemDto = itemService.updateItem(savedUser.getId(), item.getId(), newItemDto);
        assertThat(updatedItemDto.getId()).isNotNull();
        assertThat(updatedItemDto.getName()).isEqualTo("Woman Hat");
        assertThat(updatedItemDto.getDescription()).isEqualTo("Red hat with laces");
        assertThat(updatedItemDto.getAvailable()).isTrue();

        Item updatedItem = entityManager.find(Item.class, item.getId());
        assertThat(updatedItem).isNotNull();
        assertThat(updatedItem.getName()).isEqualTo("Woman Hat");
        assertThat(updatedItem.getDescription()).isEqualTo("Red hat with laces");
        assertThat(updatedItem.getAvailable()).isTrue();
    }

    @Test
    void updateItemStatus() {
        ItemDto newItemDto = new ItemDto();
        newItemDto.setAvailable(false);

        ItemDto updatedItemDto = itemService.updateItem(savedUser.getId(), item.getId(), newItemDto);
        assertThat(updatedItemDto.getId()).isNotNull();
        assertThat(updatedItemDto.getName()).isEqualTo("Woman Hat");
        assertThat(updatedItemDto.getDescription()).isEqualTo("Blue hat with laces");
        assertThat(updatedItemDto.getAvailable()).isFalse();

        Item updatedItem = entityManager.find(Item.class, item.getId());
        assertThat(updatedItem).isNotNull();
        assertThat(updatedItem.getName()).isEqualTo("Woman Hat");
        assertThat(updatedItem.getDescription()).isEqualTo("Blue hat with laces");
        assertThat(updatedItem.getAvailable()).isFalse();
    }

    @Test
    void updateItemNotExistingUser() {
        ItemDto newItemDto = new ItemDto();
        newItemDto.setAvailable(false);
        entityManager.remove(savedUser);

        final NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemService.updateItem(savedUser.getId(), item.getId(), newItemDto));
        assertEquals("Пользователь с ID " + savedUser.getId() + " не найден", exception.getMessage());
    }

    @Test
    void updateNotExistingItem() {
        ItemDto newItemDto = new ItemDto();
        newItemDto.setAvailable(false);
        itemRepository.delete(item);

        final NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemService.updateItem(savedUser.getId(), item.getId(), newItemDto));
        assertEquals("Предмет с ID " + item.getId() + " не найден", exception.getMessage());
    }

    @Test
    void updateItemNotOwner() {
        ItemDto newItemDto = new ItemDto();
        newItemDto.setAvailable(false);

        final ConditionsNotMetException exception = assertThrows(ConditionsNotMetException.class,
                () -> itemService.updateItem(savedUser2.getId(), item.getId(), newItemDto));
        assertEquals("Пользователь с ID " + savedUser2.getId() +
                " не является владельцем предмета с ID " + item.getId(), exception.getMessage());
    }

    @Test
    void updateItemNotExistingRequest() {
        ItemDto newItemDto = new ItemDto();
        newItemDto.setRequestId(request.getId());
        entityManager.remove(request);

        final NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemService.updateItem(savedUser.getId(), item.getId(), newItemDto));
        assertEquals("Запрос с ID " + request.getId() + " не найден", exception.getMessage());
    }

    @Test
    void getItemDtoById() {
        ItemDtoWithBooking foundItemDto = itemService.getItemDtoById(savedUser.getId(), item.getId());
        assertThat(foundItemDto).isNotNull();
        assertThat(foundItemDto.getName()).isEqualTo("Woman Hat");
        assertThat(foundItemDto.getDescription()).isEqualTo("Blue hat with laces");
        assertThat(foundItemDto.getAvailable()).isTrue();
    }

    @Test
    void getNotExistingItem() {
        itemRepository.delete(item);
        final NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemService.getItemDtoById(savedUser.getId(), item.getId()));
        assertEquals("Предмет с ID " + item.getId() + " не найден", exception.getMessage());
    }

    @Test
    void getUserItems() {
        List<ItemDtoWithBooking> userItemsDto = itemService.getUserItems(savedUser.getId()).stream().toList();
        assertThat(userItemsDto.size()).isEqualTo(1);
        assertThat(userItemsDto.getFirst().getName()).isEqualTo("Woman Hat");
    }

    @Test
    void getNotExistingUserItems() {
        entityManager.remove(savedUser);
        final NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemService.getUserItems(savedUser.getId()));
        assertEquals("Пользователь с ID " + savedUser.getId() + " не найден", exception.getMessage());

    }

    @Test
    void getUserItemsEmpty() {
        List<ItemDtoWithBooking> userItemsDto = itemService.getUserItems(savedUser2.getId()).stream().toList();
        assertThat(userItemsDto.size()).isEqualTo(0);
    }

    @Test
    void searchItem() {
        Collection<ItemDto> foundItemsDto = itemService.searchItem("blue");
        assertThat(foundItemsDto.size()).isEqualTo(1);
        assertThat(foundItemsDto.contains(ItemMapper.toItemDto(item))).isTrue();
    }

    @Test
    void searchItemEmpty() {
        Collection<ItemDto> foundItemsDto = itemService.searchItem("ball");
        assertThat(foundItemsDto.size()).isEqualTo(0);
    }

    @Test
    void searchItemEmptySearch() {
        Collection<ItemDto> foundItemsDto = itemService.searchItem("");
        assertThat(foundItemsDto.size()).isEqualTo(0);
    }

    @Test
    void addComment() {
        CommentDto commentDto = new CommentDto();
        commentDto.setText("Comment");

        CommentDto createdCommentDto = itemService.addComment(savedUser2.getId(), item.getId(), commentDto);
        assertThat(createdCommentDto.getId()).isNotNull();
        assertThat(createdCommentDto.getCreated()).isNotNull();
        assertThat(createdCommentDto.getItemId()).isEqualTo(item.getId());
        assertThat(createdCommentDto.getAuthorId()).isEqualTo(savedUser2.getId());
        assertThat(createdCommentDto.getAuthorName()).isEqualTo("Clothilde");
        assertThat(createdCommentDto.getText()).isEqualTo("Comment");

        Comment createdComment = entityManager.find(Comment.class, createdCommentDto.getId());
        assertThat(createdComment).isNotNull();
        assertThat(createdComment.getCreated()).isNotNull();
        assertThat(createdComment.getItemId()).isEqualTo(item.getId());
        assertThat(createdComment.getAuthorId()).isEqualTo(savedUser2.getId());
        assertThat(createdComment.getAuthorName()).isEqualTo("Clothilde");
        assertThat(createdComment.getText()).isEqualTo("Comment");
    }

    @Test
    void addCommentNotExistingUser() {
        CommentDto commentDto = new CommentDto();
        commentDto.setText("Comment");
        entityManager.remove(savedUser2);

        final NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemService.addComment(savedUser2.getId(), item.getId(), commentDto));
        assertEquals("Пользователь с ID " + savedUser2.getId() + " не найден", exception.getMessage());
    }

    @Test
    void addCommentNotExistingItem() {
        CommentDto commentDto = new CommentDto();
        commentDto.setText("Comment");
        itemRepository.delete(item);

        final NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemService.addComment(savedUser2.getId(), item.getId(), commentDto));
        assertEquals("Предмет с ID " + item.getId() + " не найден", exception.getMessage());
    }

    @Test
    void addCommentNotBooker() {
        CommentDto commentDto = new CommentDto();
        commentDto.setText("Comment");
        entityManager.remove(booking);

        final ConditionsNotMetException exception = assertThrows(ConditionsNotMetException.class,
                () -> itemService.addComment(savedUser2.getId(), item.getId(), commentDto));
        assertEquals("Оставлять комментарии можно только после аренды", exception.getMessage());
    }
}
