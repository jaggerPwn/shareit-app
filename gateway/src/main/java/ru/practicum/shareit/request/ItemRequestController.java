package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;


@Validated
@RestController
@AllArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final RequestClient requestClient;

    @Validated
    @PostMapping()
    public ResponseEntity<Object> saveRequest(
            @Valid @RequestBody ItemRequestDto itemRequestDto,
            @RequestHeader("X-Sharer-User-Id") Integer userId) {
        return requestClient.saveRequest(itemRequestDto, userId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getRequestById(
            @PathVariable int id,
            @RequestHeader("X-Sharer-User-Id") Integer userId) {
        return requestClient.getRequestById(id, userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getRequestPage(
            @RequestHeader("X-Sharer-User-Id") Integer userId,
            @Positive @RequestParam(name = "size", defaultValue = "10", required = false) Integer size,
            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0", required = false) Integer from) {
        return requestClient.getAllRequestPage(userId, size, from);
    }

    @GetMapping
    public ResponseEntity<Object> getRequestsOfRequestor(
            @RequestHeader("X-Sharer-User-Id") Integer userId) {
        return requestClient.getRequestsOfRequestor(userId);
    }

    @DeleteMapping
    public void deleteAllRequests() {
        requestClient.deleteAll();
    }
}
