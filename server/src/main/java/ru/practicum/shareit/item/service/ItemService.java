package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    ItemDto saveItem(ItemDto itemDto, int userId);

    ItemDto updateItem(ItemDto itemDto, int itemId, int userId);

    ItemDto getItem(int itemId, int userId);

    List<ItemDto> getItemsByUser(int userId, Integer size, Integer from);

    List<ItemDto> searchItem(String text);

    void deleteItems();

    boolean validateIfItemAvailable(int itemId);

    Item getItemById(int itemId);

    CommentDto saveComment(CommentDto commentDto, int itemId, int userId);

    void deleteComments();

    List<ItemDto> getItemByRequestId(int requestId);
}
