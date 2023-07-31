package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDtoIdAndBooker;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.ValidationException404;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.validation.ItemValidator;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.validation.UserValidator;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingService bookingService;
    @PersistenceContext
    EntityManager entityManager;

    @Override
    public ItemDto saveItem(ItemDto itemDto, int userId) {
        ItemValidator.saveValidation(itemDto);
        Item item = ItemMapper.dtoToItem(itemDto);
        Optional<User> existingUserFromRepository = userRepository.findById(userId);
        item.setUser(existingUserFromRepository.orElseThrow(() -> new ValidationException404("user  " + userId +
                " not found")));
        itemRepository.save(item);
        return ItemMapper.itemToDto(itemRepository.findById(item.getId()).orElseThrow(() ->
                new ValidationException404("item  " + item.getId() + " not found")));
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto, int itemId, int userId) {
        ItemValidator.updateValidation(itemDto, itemId, userId, itemRepository);
        Item itemNew = ItemMapper.dtoToItem(itemDto);
        itemDto.setId(itemId);
        Optional<User> existingUserFromRepository = userRepository.findById(userId);
        itemNew.setUser(existingUserFromRepository.orElseThrow(() -> new ValidationException404("user  " + userId +
                " not found")));
        Optional<Item> existingItemFromRepository = itemRepository.findById(itemId);
        if (existingItemFromRepository.isEmpty()) throw new ValidationException404("itemNew  " + itemId +
                " not found");
        Item itemOld = existingItemFromRepository.get();
        if (itemNew.getAvailable() != null) itemOld.setAvailable(itemNew.getAvailable());
        if (itemNew.getDescription() != null) itemOld.setDescription(itemNew.getDescription());
        if (itemNew.getName() != null) itemOld.setName(itemNew.getName());
        itemNew = itemOld;
        return ItemMapper.itemToDto(itemRepository.save(itemNew));
    }

    @Override
    public ItemDto getItem(int itemId, int userId) {
        UserValidator.validateIfUserExists(userId, userRepository);
        ItemDto itemDto = ItemMapper.itemToDto(itemRepository.findById(itemId).orElseThrow(() ->
                new ValidationException404("item  " + itemId + " not found")));
        BookingDtoIdAndBooker nextBooking = bookingService.findNextBookingByItemId(itemId);
        itemDto.setNextBooking(nextBooking);
        return itemDto;
    }

    @Override
    public List<ItemDto> getItemsByUser(int userId) {
        UserValidator.validateIfUserExists(userId, userRepository);
        return ItemMapper.itemToDtoList(itemRepository.findByUserId(userId));
    }

    @Override
    public List<ItemDto> searchItem(String description) {
        if (description.isBlank()) return new ArrayList<>();
        return ItemMapper.itemToDtoList(itemRepository.findByDescriptionContainingIgnoreCaseAndAvailableTrue(description));
    }

    @Override
    @Transactional
    public void deleteItems() {
        itemRepository.deleteAll();
        entityManager
                .createNativeQuery("ALTER TABLE  ITEMS ALTER COLUMN ID  RESTART WITH 1;")
                .executeUpdate();
    }

    @Override
    public boolean validateIfItemAvailable(int itemId) {
        if (itemRepository.findById(itemId).orElseThrow(() -> new ValidationException404("item not found"))
                .getAvailable()) return true;
        return false;
    }

    @Override
    public Item getItemOwner(int itemId) {
        return itemRepository.findById(itemId).orElseThrow(() -> new ValidationException404("item not found" + itemId));
    }
}
