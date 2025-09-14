package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.ConditionsNotMetException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.RequestMapper;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public RequestDto addRequest(Long userId, RequestDto requestDto) {
        User requestor = checkUser(userId);
        requestDto.setRequestor(requestor);
        requestDto.setCreated(LocalDateTime.now());
        Request request = RequestMapper.toRequest(requestDto);
        return RequestMapper.toRequestDto(requestRepository.save(request));
    }

    @Override
    public Collection<RequestDto> getUserRequests(Long userId) {
        checkUser(userId);
        List<Request> userRequests = requestRepository.getByRequestorIdOrderByCreatedDesc(userId);
        List<Long> userRequestsId = new ArrayList<>();
        for (Request request:userRequests) userRequestsId.add(request.getId());
        List<Item> requestedItems = itemRepository.findByRequestIn(userRequestsId);
        for (Request request:userRequests) {
            List<Item> requestItems = requestedItems.stream()
                            .filter(i -> i.getRequest().equals(request.getId()))
                                    .toList();
            request.setItems(requestItems);
        }

        return userRequests.stream()
                .map(RequestMapper::toRequestDto)
                .collect(Collectors.toSet());
    }

    @Override
    public Collection<RequestDto> getAllRequests() {
        return requestRepository.findAll(Sort.sort(LocalDateTime.class)).stream()
                .map(RequestMapper::toRequestDto)
                .collect(Collectors.toSet());
    }

    @Override
    public RequestDto findById(Long requestId) {
        Optional<Request> requestOpt = Optional.of(requestRepository.findById(requestId)
                .orElseThrow(() -> new ConditionsNotMetException("Запрос с ID " + requestId + " не найден")));
        Request request = requestOpt.get();
        List<Item> requestItems = itemRepository.findByRequest(requestId);
        request.setItems(requestItems);
        return RequestMapper.toRequestDto(request);
    }

    private User checkUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + userId + " не найден"));
    }
}
