package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ItemDto saveItem(@Valid @RequestBody ItemDto itemDto, @RequestHeader("X-Sharer-User-Id") int userId) {
        return itemService.saveItem(itemDto, userId);
    }

    @PatchMapping("{itemId}")
    public ItemDto updateItem(@RequestBody ItemDto itemDto, @PathVariable int itemId,
                              @RequestHeader("X-Sharer-User-Id") int userId) {
        return itemService.updateItem(itemDto, itemId, userId);
    }

    @GetMapping("{itemId}")
    public ItemDto getItem(@PathVariable int itemId, @RequestHeader("X-Sharer-User-Id") int userId) {
        return itemService.getItem(itemId, userId);
    }

    @GetMapping
    public List<ItemDto> getItemsByUser(@RequestHeader("X-Sharer-User-Id") int userId,
                                        @RequestParam(name = "size", required = false) Integer size) {
        return itemService.getItemsByUser(userId, size);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam("text") String text) {
        return itemService.searchItem(text);
    }

    @DeleteMapping
    public void deleteItems() {
        itemService.deleteItems();
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto saveComment(@Valid @RequestBody CommentDto commentDto, @PathVariable int itemId, @RequestHeader("X-Sharer-User-Id") int userId) {
        return itemService.saveComment(commentDto, itemId, userId);
    }

    @DeleteMapping("/comments")
    public void deleteComments() {
        itemService.deleteComments();
    }

}
