package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.validation.Marker;

import java.util.Collection;

/**
 * TODO Sprint add-item-requests.
 */
@Validated
@RestController
@RequestMapping(path = "/requests")
public class RequestController {
    private final RequestService requestService;
    private final String userIdHeader = "X-Sharer-User-Id";

    @Autowired
    public RequestController(RequestService requestService) {
        this.requestService = requestService;
    }

    @PostMapping
    @Validated({Marker.OnCreate.class})
    public RequestDto addRequest(@RequestHeader(userIdHeader) Long userId,
                                 @Valid @RequestBody RequestDto requestDto) {
        return requestService.addRequest(userId, requestDto);
    }

    @GetMapping
    public Collection<RequestDto> getUserRequests(@RequestHeader(userIdHeader) Long userId) {
        return requestService.getUserRequests(userId);
    }

    @GetMapping("/all")
    public Collection<RequestDto> getAll() {
        return requestService.getAllRequests();
    }

    @GetMapping("/{requestId}")
    public RequestDto findById(@PathVariable Long requestId) {
        return requestService.findById(requestId);
    }
}
