package ru.practicum.shareit.item.validation;

import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.exception.ValidationException400;
import ru.practicum.shareit.exception.ValidationException404;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.repository.ItemRepository;

@Slf4j
public class ItemValidator {
    public static void saveValidation(ItemDto itemDto) {
        if (!itemDto.getAvailable()) {
            log.error("user tried to save unavailable item: " + itemDto);
            throw new ValidationException400("only available items must be created");
        }
        log.debug("Item validation is successful");
    }

    public static void updateValidation(ItemDto itemDto, int itemId, int userid, ItemRepository itemRepository) {
        if (itemRepository.findById(itemId)
                .orElseThrow(() -> new ValidationException404("item " + itemId + " not found"))
                .getUser().getId() != userid) {
            log.error("User " + userid + " tried to update entity created by another user");
            throw new ValidationException404("User " + userid + " tried to update entity created by another user");
        }
        if (itemDto.getId() != 0 && itemDto.getId() != itemId) {
            log.error("User tried to update item with PathVariable " + itemId + " and different id in RequestBody " +
                    itemDto.getId());
            throw new ValidationException400("id of item " + itemId + " and id in params " +
                    itemDto.getId() + " doesn't match");
        }
        log.debug("Item validation is successful");
    }

}
