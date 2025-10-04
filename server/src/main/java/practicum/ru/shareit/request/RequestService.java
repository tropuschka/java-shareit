package practicum.ru.shareit.request;

import practicum.ru.shareit.request.dto.RequestDto;

import java.util.Collection;

public interface RequestService {
    RequestDto addRequest(Long userId, RequestDto requestDto);

    Collection<RequestDto> getUserRequests(Long userId);

    Collection<RequestDto> getAllRequests();

    RequestDto findById(Long requestId);
}
