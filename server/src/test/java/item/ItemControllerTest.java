package item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import practicum.ru.shareit.exceptions.ConditionsNotMetException;
import practicum.ru.shareit.item.ItemController;
import practicum.ru.shareit.item.ItemService;
import practicum.ru.shareit.item.dto.CommentDto;
import practicum.ru.shareit.item.dto.ItemDto;
import practicum.ru.shareit.item.dto.ItemDtoWithBooking;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class ItemControllerTest {
    @Mock
    ItemService itemService;
    @InjectMocks
    ItemController itemController;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private MockMvc mockMvc;
    private ItemDto itemDto;
    private ItemDto returnItemDto;
    private ItemDtoWithBooking returnItemDtoWithBooking;
    private CommentDto commentDto;
    private CommentDto returnCommentDto;
    private final String USER_ID_HEADER = "X-Sharer-User-Id";

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(itemController).build();
        itemDto = new ItemDto();
        itemDto.setName("Item");
        itemDto.setDescription("Description");
        itemDto.setAvailable(true);
        returnItemDto = itemDto;
        returnItemDto.setId(1L);
        returnItemDtoWithBooking = new ItemDtoWithBooking();
        returnItemDtoWithBooking.setName("Item");
        returnItemDtoWithBooking.setDescription("Description");
        returnItemDtoWithBooking.setAvailable(true);
        returnItemDtoWithBooking.setId(1L);
        commentDto = new CommentDto();
        commentDto.setText("Comment");
        returnCommentDto = commentDto;
        returnCommentDto.setId(1L);
        returnCommentDto.setItemId(1L);
        returnCommentDto.setAuthorId(1L);
        returnCommentDto.setAuthorName("Primitivus");
    }

    @Test
    public void createItem() throws Exception {
        when(itemService.addItem(anyLong(), any(ItemDto.class))).thenReturn(returnItemDto);

        mockMvc.perform(post("/items")
                    .header(USER_ID_HEADER, 1)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
        verify(itemService, times(1)).addItem(1L, itemDto);
    }

    @Test
    public void createItemWithoutName() throws Exception {
        itemDto.setName("");
        when(itemService.addItem(anyLong(), any(ItemDto.class)))
                .thenThrow(new ConditionsNotMetException("Название предмета не должно быть пустым"));

        final ConditionsNotMetException exception = Assertions.assertThrows(ConditionsNotMetException.class,
                () -> itemService.addItem(1L, itemDto));
        Assertions.assertEquals("Название предмета не должно быть пустым", exception.getMessage());
    }

    @Test
    public void createItemWithoutDescription() throws Exception {
        itemDto.setDescription("");
        when(itemService.addItem(anyLong(), any(ItemDto.class)))
                .thenThrow(new ConditionsNotMetException("Описание предмета не должно быть пустым"));

        final ConditionsNotMetException exception = Assertions.assertThrows(ConditionsNotMetException.class,
                () -> itemService.addItem(1L, itemDto));
        Assertions.assertEquals("Описание предмета не должно быть пустым", exception.getMessage());
    }

    @Test
    public void createItemWithoutStatus() throws Exception {
        itemDto.setAvailable(null);
        when(itemService.addItem(anyLong(), any(ItemDto.class)))
                .thenThrow(new ConditionsNotMetException("Статус должен быть указан"));

        final ConditionsNotMetException exception = Assertions.assertThrows(ConditionsNotMetException.class,
                () -> itemService.addItem(1L, itemDto));
        Assertions.assertEquals("Статус должен быть указан", exception.getMessage());
    }

    @Test
    public void changeItem() throws  Exception {
        when(itemService.updateItem(anyLong(), anyLong(), any(ItemDto.class))).thenReturn(itemDto);

        mockMvc.perform(patch("/items/1")
                        .header(USER_ID_HEADER, 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
        verify(itemService, times(1)).updateItem(1L, 1L, itemDto);
    }

    @Test
    public void findItemById() throws Exception {
        when(itemService.getItemDtoById(anyLong(), anyLong())).thenReturn(returnItemDtoWithBooking);

        mockMvc.perform(get("/items/1")
                .header(USER_ID_HEADER, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
        verify(itemService, times(1)).getItemDtoById(1L, 1L);
    }

    @Test
    public void findUserItems() throws Exception {
        when(itemService.getUserItems(anyLong())).thenReturn(List.of(returnItemDtoWithBooking));

        mockMvc.perform(get("/items")
                .header(USER_ID_HEADER, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].description").value("Description"));
        verify(itemService, times(1)).getUserItems(1L);
    }

    @Test
    public void searchItem() throws Exception {
        when(itemService.searchItem(anyString())).thenReturn(List.of(returnItemDto));

        mockMvc.perform(get("/items/search")
                .param("text", "descr"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].description").value("Description"));
        verify(itemService, times(1)).searchItem("descr");
    }

    @Test
    public void addComment() throws Exception {
        when(itemService.addComment(anyLong(), anyLong(), any(CommentDto.class))).thenReturn(returnCommentDto);

        mockMvc.perform(post("/items/1/comment")
                .header(USER_ID_HEADER, 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
        verify(itemService, times(1)).addComment(1L, 1L, commentDto);
    }

    @Test
    public void addCommentWithoutText() throws Exception {
        commentDto.setText("");
        when(itemService.addComment(anyLong(), anyLong(), any(CommentDto.class)))
                .thenThrow(new ConditionsNotMetException("Текст комментария не должен быть пустым"));

        final ConditionsNotMetException exception = Assertions.assertThrows(ConditionsNotMetException.class,
                () -> itemService.addComment(1L, 1L, commentDto));
        Assertions.assertEquals("Текст комментария не должен быть пустым", exception.getMessage());
    }
}
