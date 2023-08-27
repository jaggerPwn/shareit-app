package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto saveRequest(@RequestBody ItemRequestDto itemRequestDto,
                                      @RequestHeader("X-Sharer-User-Id") int userId) {
        return itemRequestService.saveRequest(itemRequestDto, userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getRequestPage(@RequestHeader("X-Sharer-User-Id") int userId,
                                               @RequestParam(name = "size", required = false) Integer size,
                                               @RequestParam(name = "from", required = false) Integer from) {
        return itemRequestService.getAllRequestPage(userId, size, from);
    }

    @GetMapping
    public List<ItemRequestDto> getRequestsOfRequestor(@RequestHeader("X-Sharer-User-Id") int userId) {
        return itemRequestService.getRequestsOfRequestor(userId);
    }

    @GetMapping("/{id}")
    public ItemRequestDto getRequestById(@PathVariable int id, @RequestHeader("X-Sharer-User-Id") int userId) {
        return itemRequestService.getRequestById(id, userId);
    }

    @DeleteMapping
    public void deleteAllRequests() {
        itemRequestService.deleteAll();
    }
}
