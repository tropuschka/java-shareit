package practicum.ru.shareit.item;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import practicum.ru.shareit.item.dto.CommentDto;
import practicum.ru.shareit.item.dto.ItemDto;
import practicum.ru.shareit.validation.Marker;

@Validated
@RequestMapping(path = "/items")
@RestController
public class ItemController {
    private final ItemClient itemClient;
    private final String userIdHeader = "X-Sharer-User-Id";

    @Autowired
    public ItemController(ItemClient itemClient) {
        this.itemClient = itemClient;
    }

    @PostMapping
    @Validated({Marker.OnCreate.class})
    public ResponseEntity<Object> addItem(@RequestHeader(userIdHeader) Long userId,
                                          @Valid @RequestBody ItemDto itemDto) {
        return itemClient.addItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    @Validated({Marker.OnUpdate.class})
    public ResponseEntity<Object> changeItem(@RequestHeader(userIdHeader) Long userId,
                              @PathVariable Long itemId,
                              @Valid @RequestBody ItemDto itemDto) {
        return itemClient.updateItem(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> findItemById(@RequestHeader(userIdHeader) Long userId,
                                           @PathVariable Long itemId) {
        return itemClient.getItem(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> findUserItems(@RequestHeader(userIdHeader) Long userId) {
        return itemClient.getUserItems(userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItem(@RequestParam String text) {
        return itemClient.searchItem(text);
    }

    @PostMapping("/{itemId}/comment")
    @Validated({Marker.OnCreate.class})
    public ResponseEntity<Object> addComment(@RequestHeader(userIdHeader) Long userId,
                                 @PathVariable Long itemId,
                                 @Valid @RequestBody CommentDto comment) {
        return itemClient.addComment(userId, itemId, comment);
    }
}
