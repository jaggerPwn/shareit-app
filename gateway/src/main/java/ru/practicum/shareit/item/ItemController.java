package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.validation.CreationValidation;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemClient itemClient;

    @PostMapping
    @Validated(CreationValidation.class)
    public ResponseEntity<Object> saveItem(@Valid @RequestBody ItemDto itemDto,
                                           @RequestHeader("X-Sharer-User-Id") Integer userId) {
        return itemClient.saveItem(itemDto, userId);
    }

    @Validated
    @PatchMapping("{itemId}")
    public ResponseEntity<Object> updateItem(@RequestBody ItemDto itemDto, @PathVariable int itemId,
                                             @RequestHeader("X-Sharer-User-Id") int userId) {
        return itemClient.updateItem(itemDto, itemId, userId);
    }

    @GetMapping("{itemId}")
    public ResponseEntity<Object> findItem(@PathVariable Integer itemId, @RequestHeader("X-Sharer-User-Id") Integer userId) {
        return itemClient.findItem(itemId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getItemsByUser(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                                 @Positive @RequestParam(name = "size", defaultValue = "10", required = false) Integer size,
                                                 @PositiveOrZero @RequestParam(name = "from", defaultValue = "0", required = false) Integer from
    ) {
        return itemClient.getItemsByUser(userId, size, from);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(
            @NotNull @RequestParam(required = false) String text) {
        return itemClient.searchItem(text);
    }

    @DeleteMapping
    public void deleteItems() {
        itemClient.deleteAll();
    }

    @Validated
    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> saveComment(@Valid @RequestBody CommentDto commentDto,
                                              @PathVariable int itemId,
                                              @RequestHeader("X-Sharer-User-Id") Integer userId) {
        if (userId == null) throw new IllegalArgumentException("User cant be empty");
        return itemClient.saveComment(commentDto, itemId, userId);
    }

    @DeleteMapping("/comments")
    public void deleteComments() {
        itemClient.deleteComments();
    }

}
