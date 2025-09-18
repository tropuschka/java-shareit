package practicum.ru.shareit.request;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.DefaultUriBuilderFactory;
import practicum.ru.shareit.client.BaseClient;
import practicum.ru.shareit.request.dto.RequestDto;

public class RequestClient extends BaseClient {
    private static final String API_PREFIX = "/requests";

    public RequestClient(RestTemplateBuilder builder, @Value("${shareit-server.url}") String serverUrl) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .build()
        );
    }

    public ResponseEntity<Object> addRequest(Long userId, RequestDto dto) {
        return post("", userId, dto);
    }

    public ResponseEntity<Object> getUserRequests(Long userId) {
        return get("", userId);
    }

    public ResponseEntity<Object> getAllRequests(Long userId) {
        return get("/all", userId);
    }

    public ResponseEntity<Object> findById(Long userId, Long requestId) {
        return get("/" + requestId, userId);
    }
}
