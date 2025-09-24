package practicum.ru.shareit.request;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import practicum.ru.shareit.request.dto.RequestDto;
import practicum.ru.shareit.validation.Marker;

@Controller
@RequestMapping(path = "/requests")
@Validated
public class RequestController {
    private final RequestClient requestClient;
    private final String userIdHeader = "X-Sharer-User-Id";

    @Autowired
    public RequestController(RequestClient requestClient) {
        this.requestClient = requestClient;
    }

    @PostMapping
    @Validated({Marker.OnCreate.class})
    public ResponseEntity<Object> addRequest(@RequestHeader(userIdHeader) Long userId,
                                             @Valid @RequestBody RequestDto requestDto) {
        return requestClient.addRequest(userId, requestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getUserRequests(@RequestHeader(userIdHeader) Long userId) {
        return requestClient.getUserRequests(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAll(@RequestHeader(userIdHeader) Long userId) {
        return requestClient.getAllRequests(userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> findById(@RequestHeader(userIdHeader) Long userId, @PathVariable Long requestId) {
        return requestClient.findById(userId, requestId);
    }
}
