package practicum.ru.shareit.item;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import practicum.ru.shareit.client.BaseClient;
import practicum.ru.shareit.item.dto.CommentDto;
import practicum.ru.shareit.item.dto.ItemDto;
import practicum.ru.shareit.validation.Marker;

@Validated
@RequestMapping(path = "/items")
@RestController
public class ItemController {
    private final ItemClient itemClient;

    @Autowired
    public ItemController(ItemClient itemClient) {
        this.itemClient = itemClient;
    }

    @PostMapping
    @Validated({Marker.OnCreate.class})
    public ResponseEntity<Object> addItem(@RequestHeader(BaseClient.userIdHeader) Long userId,
                                          @Valid @RequestBody ItemDto itemDto) {
        return itemClient.addItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    @Validated({Marker.OnUpdate.class})
    public ResponseEntity<Object> changeItem(@RequestHeader(BaseClient.userIdHeader) Long userId,
                              @PathVariable Long itemId,
                              @Valid @RequestBody ItemDto itemDto) {
        return itemClient.updateItem(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> findItemById(@RequestHeader(BaseClient.userIdHeader) Long userId,
                                           @PathVariable Long itemId) {
        return itemClient.getItem(itemId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> findUserItems(@RequestHeader(BaseClient.userIdHeader) Long userId) {
        return itemClient.getUserItems(userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItem(@RequestParam String text) {
        return itemClient.searchItem(text);
    }

    @PostMapping("/{itemId}/comment")
    @Validated({Marker.OnCreate.class})
    public ResponseEntity<Object> addComment(@RequestHeader(BaseClient.userIdHeader) Long userId,
                                 @PathVariable Long itemId,
                                 @Valid @RequestBody CommentDto comment) {
        return itemClient.addComment(userId, itemId, comment);
    }
}
