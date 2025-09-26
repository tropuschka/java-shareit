package practicum.ru.shareit.request.dto;

import practicum.ru.shareit.request.Request;
import practicum.ru.shareit.user.User;
import practicum.ru.shareit.user.dto.UserMapper;

import java.util.ArrayList;

public class RequestMapper {
    public static RequestDto toRequestDto(Request request) {
        return new RequestDto(
                request.getId(),
                request.getDescription(),
                UserMapper.toUserDto(request.getRequestor()),
                request.getCreated(),
                new ArrayList<>()
        );
    }

    public static Request toRequest(RequestDto requestDto) {
        Request request = new Request();
        request.setId(requestDto.getId());
        request.setDescription(requestDto.getDescription());
        User requestor = null;
        if (requestDto.getRequestor() != null) requestor = UserMapper.toUser(requestDto.getRequestor());
        request.setRequestor(requestor);
        request.setCreated(requestDto.getCreated());
        return request;
    }
}
