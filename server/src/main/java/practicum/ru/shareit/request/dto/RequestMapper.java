package practicum.ru.shareit.request.dto;

import practicum.ru.shareit.request.Request;

public class RequestMapper {
    public static RequestDto toRequestDto(Request request) {
        return new RequestDto(
                request.getId(),
                request.getDescription(),
                request.getRequestor(),
                request.getCreated()
        );
    }

    public static Request toRequest(RequestDto requestDto) {
        Request request = new Request();
        request.setId(requestDto.getId());
        request.setDescription(requestDto.getDescription());
        request.setRequestor(requestDto.getRequestor());
        request.setCreated(requestDto.getCreated());
        return request;
    }
}
