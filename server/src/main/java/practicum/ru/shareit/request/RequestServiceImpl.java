package practicum.ru.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import practicum.ru.shareit.exceptions.NotFoundException;
import practicum.ru.shareit.item.Item;
import practicum.ru.shareit.item.ItemRepository;
import practicum.ru.shareit.item.dto.ItemDto;
import practicum.ru.shareit.item.dto.ItemMapper;
import practicum.ru.shareit.request.dto.RequestDto;
import practicum.ru.shareit.request.dto.RequestMapper;
import practicum.ru.shareit.user.User;
import practicum.ru.shareit.user.UserRepository;
import practicum.ru.shareit.user.dto.UserMapper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public RequestDto addRequest(Long userId, RequestDto requestDto) {
        User requestor = checkUser(userId);
        requestDto.setCreated(LocalDateTime.now());
        Request request = RequestMapper.toRequest(requestDto);
        request.setRequestor(requestor);
        return RequestMapper.toRequestDto(requestRepository.save(request));
    }

    @Override
    public Collection<RequestDto> getUserRequests(Long userId) {
        checkUser(userId);
        List<Request> userRequests = requestRepository.getByRequestorIdOrderByCreatedDesc(userId);
        List<Long> requestIds = new ArrayList<>();
        for (Request request : userRequests) requestIds.add(request.getId());
        List<Item> providedItems = itemRepository.findByRequestIdIn(requestIds);
        List<RequestDto> userRequestDtos = userRequests.stream()
                .map(RequestMapper::toRequestDto)
                .toList();
        for (RequestDto request : userRequestDtos) {
            List<ItemDto> requestItems = new ArrayList<>();
            for (Item item : providedItems) {
                if (item.getRequestId().equals(request.getId())) requestItems.add(ItemMapper.toItemDto(item));
            }
            request.setItems(requestItems);
        }
        return userRequestDtos;
    }

    @Override
    public Collection<RequestDto> getAllRequests() {
        List<RequestDto> userRequestDtos = requestRepository.findAll(Sort.by("created").descending()).stream()
                .map(RequestMapper::toRequestDto).toList();
        List<Long> requestIds = new ArrayList<>();
        for (RequestDto request : userRequestDtos) requestIds.add(request.getId());
        List<Item> providedItems = itemRepository.findByRequestIdIn(requestIds);
        for (RequestDto request : userRequestDtos) {
            List<ItemDto> requestItems = new ArrayList<>();
            for (Item item : providedItems) {
                if (item.getRequestId().equals(request.getId())) requestItems.add(ItemMapper.toItemDto(item));
            }

            request.setItems(requestItems);
        }
        return userRequestDtos;
    }

    @Override
    public RequestDto findById(Long requestId) {
        Optional<Request> requestOpt = Optional.of(requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос с ID " + requestId + " не найден")));
        RequestDto request = RequestMapper.toRequestDto(requestOpt.get());
        List<ItemDto> requestItems = itemRepository.findByRequestIdIn(List.of(request.getId())).stream()
                        .map(ItemMapper::toItemDto).toList();
        request.setItems(requestItems);
        return request;
    }

    private User checkUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + userId + " не найден"));
    }
}
