package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.validation.ItemValidator;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.validation.UserValidator;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserService userService;

    @Override
    public ItemDto saveItem(ItemDto itemDto, int userId) {
        UserValidator.validateIfUserExists(userId, userService);
        ItemValidator.saveValidation(itemDto);
        Item item = ItemMapper.dtoToItem(itemDto);
        item.setOwnerId(userId);
        return itemRepository.saveItem(item, userId);
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto, int itemId, int userId) {
        UserValidator.validateIfUserExists(userId, userService);
        ItemValidator.updateValidation(itemDto, itemId);
        Item item = ItemMapper.dtoToItem(itemDto);
        item.setOwnerId(userId);
        return itemRepository.updateItem(item, itemId, userId);
    }

    @Override
    public ItemDto getItem(int itemId, int userId) {
        UserValidator.validateIfUserExists(userId, userService);
        return itemRepository.getItem(itemId, userId);
    }

    @Override
    public List<ItemDto> getItemsByUser(int userId) {
        UserValidator.validateIfUserExists(userId, userService);
        return itemRepository.getItemsByUser(userId);
    }

    @Override
    public List<ItemDto> searchItem(String text) {
        return itemRepository.searchItem(text);
    }

    @Override
    public void deleteItems() {
        itemRepository.deleteItems();
    }
}
