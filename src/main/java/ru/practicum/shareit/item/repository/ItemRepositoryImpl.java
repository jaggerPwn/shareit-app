package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.ValidationException400;
import ru.practicum.shareit.exception.ValidationException404;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class ItemRepositoryImpl implements ItemRepository {
    private final HashMap<Integer, Item> items = new HashMap<>();
    private int nextId;

    @Override
    public ItemDto saveItem(Item item, int userId) {
        item.setId(getId());
        items.put(item.getId(), item);
        return ItemMapper.itemToDto(item);
    }

    @Override
    public ItemDto updateItem(Item itemUpdated, int itemId, int userId) {
        Item itemInBase;
        try {
            itemInBase = items.get(itemId);
        } catch (NullPointerException e) {
            throw new ValidationException404("itemUpdated " + itemId + " not found");
        }

        if (itemInBase.getOwnerId() != userId)
            throw new ValidationException404("User " + userId + " is not owner of item " + itemInBase);

        if (itemUpdated.getAvailable() != null)
            if (itemUpdated.getAvailable() != itemInBase.getAvailable())
                itemInBase.setAvailable(itemUpdated.getAvailable());
            else
                throw new ValidationException400("Available in base is " + itemInBase.getAvailable() +
                        " cant be changed to updeted item " + itemUpdated.getAvailable());
        if (itemUpdated.getName() != null)
            itemInBase.setName(itemUpdated.getName());
        if (itemUpdated.getDescription() != null)
            itemInBase.setDescription(itemUpdated.getDescription());
        items.put(itemId, itemInBase);
        return ItemMapper.itemToDto(itemInBase);
    }

    @Override
    public ItemDto getItem(int itemId, int userId) {
        return ItemMapper.itemToDto(items.get(itemId));
    }

    @Override
    public List<ItemDto> getItemsByUser(int userId) {
        List<Item> itemList = items.values().stream().filter(item -> item.getOwnerId() == userId)
                .collect(Collectors.toList());
        return ItemMapper.itemToDtoList(itemList);
    }

    @Override
    public List<ItemDto> searchItem(String text) {
        List<Item> itemList = new ArrayList<>();
        for (Item item : items.values()) {
            if (item.getDescription().toLowerCase().contains(text.toLowerCase()) ||
                    item.getName().toLowerCase().contains(text.toLowerCase()))
                if (item.getAvailable() && !text.isBlank())
                    itemList.add(item);
        }
        return ItemMapper.itemToDtoList(itemList);
    }

    @Override
    public void deleteItems() {
        items.clear();
        nextId = 0;
    }

    private int getId() {
        nextId++;
        return nextId;
    }
}
