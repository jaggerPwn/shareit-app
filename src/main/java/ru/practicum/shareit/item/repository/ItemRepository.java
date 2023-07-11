package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {
    ItemDto saveItem(Item item, int userId);

    ItemDto updateItem(Item item, int itemId, int userId);

    ItemDto getItem(int itemId, int userId);

    List<ItemDto> getItemsByUser(int userId);

    List<ItemDto> searchItem(String text);

    void deleteItems();
}
