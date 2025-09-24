package practicum.ru.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import practicum.ru.shareit.exceptions.ConditionsNotMetException;
import practicum.ru.shareit.exceptions.NotFoundException;
import practicum.ru.shareit.request.dto.RequestDto;
import practicum.ru.shareit.request.dto.RequestMapper;
import practicum.ru.shareit.user.User;
import practicum.ru.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;

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
        return userRequests.stream()
                .map(RequestMapper::toRequestDto)
                .collect(Collectors.toSet());
    }

    @Override
    public Collection<RequestDto> getAllRequests() {
        return requestRepository.findAll(Sort.by("created").descending()).stream()
                .map(RequestMapper::toRequestDto)
                .collect(Collectors.toSet());
    }

    @Override
    public RequestDto findById(Long requestId) {
        Optional<Request> requestOpt = Optional.of(requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос с ID " + requestId + " не найден")));
        Request request = requestOpt.get();
        return RequestMapper.toRequestDto(request);
    }

    private User checkUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + userId + " не найден"));
    }
}
