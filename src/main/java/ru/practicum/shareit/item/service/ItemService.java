package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto saveItem(ItemDto itemDto, int userId);

    ItemDto updateItem(ItemDto itemDto, int itemId, int userId);

    ItemDto getItem(int itemId, int userId);

    boolean validateIfUserHasRights(int ItemId, int UserId);

    List<ItemDto> getItemsByUser(int userId);

    List<ItemDto> searchItem(String text);

    void deleteItems();
}
