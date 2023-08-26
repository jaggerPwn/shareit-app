package ru.practicum.shareit.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.Map;

@Service
public class RequestClient extends BaseClient {
    private static final String API_PREFIX = "/requests";

    @Autowired
    public RequestClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> saveRequest(
            ItemRequestDto itemRequestDto, Integer userId) {
        return post("", userId, itemRequestDto);
    }

    public ResponseEntity<Object> getRequestById(Integer requestId, Integer userId) {
        return get("/" + requestId, userId);
    }

    public ResponseEntity<Object> getAllRequestPage(Integer userId, Integer size, Integer from) {
        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size
        );
        return get("/all" + "?from={from}&size={size}", userId, parameters);
    }
    public ResponseEntity<Object> getRequestsOfRequestor(Integer userId) {
        return get("/", userId);
    }

    public void deleteAll() {
        delete("");
    }
}
