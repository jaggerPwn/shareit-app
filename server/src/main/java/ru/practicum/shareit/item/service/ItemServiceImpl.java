package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDtoIdAndBooker;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.ValidationException400;
import ru.practicum.shareit.exception.ValidationException404;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.validation.ItemValidator;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.validation.UserValidator;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final CommentRepository commentRepository;
    private final UserService userService;
    private final BookingService bookingService;

    @Override
    public ItemDto saveItem(ItemDto itemDto, int userId) {
        ItemValidator.saveValidation(itemDto);
        Item item = ItemMapper.dtoToItem(itemDto);
        UserDto existingUserFromRepository = userService.getUserById(userId);
        item.setUser(UserMapper.dtoToUser(existingUserFromRepository));
        itemRepository.save(item);
        return ItemMapper.itemToDto(itemRepository.findById(item.getId()).orElseThrow(() ->
                new ValidationException404("item  " + item.getId() + " not found")));
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto, int itemId, int userId) {
        ItemValidator.updateValidation(itemDto, itemId, userId, itemRepository);
        Item itemNew = ItemMapper.dtoToItem(itemDto);
        itemDto.setId(itemId);
        UserDto existingUserFromRepository = userService.getUserById(userId);
        itemNew.setUser(UserMapper.dtoToUser(existingUserFromRepository));
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
        UserValidator.validateIfUserExists(userId, userService);

        ItemDto itemDto = ItemMapper.itemToDto(itemRepository.findById(itemId).orElseThrow(() ->
                new ValidationException404("item  " + itemId + " not found")));
        tryToAddNextAndLastBooking(itemId, userId, itemDto);
        try {
            itemDto.setComments(CommentMapper.ListCommentToCommentDto(commentRepository.findAllByItemId(itemId)));
        } catch (Exception e) {
            log.debug("comment for item " + itemDto + " not found");
        }
        return itemDto;
    }

    @Override
    public List<ItemDto> getItemsByUser(int userId, Integer size, Integer from) {
        UserValidator.validateIfUserExists(userId, userService);

        List<Item> itemsList;
        if (size != null && from != null) {
            itemsList = getItemsAsPage(userId, size, from);
        } else {
            itemsList = itemRepository.findByUserIdOrderByIdAsc(userId);
        }

        List<ItemDto> itemDtos = ItemMapper.itemToDtoList(itemsList);

        for (ItemDto itemDto : itemDtos) {
            tryToAddNextAndLastBooking(itemDto.getId(), userId, itemDto);
        }
        return itemDtos;
    }

    @NotNull
    private List<Item> getItemsAsPage(int userId, Integer size, Integer from) {
        Pageable pageRequest = createPageRequestUsing(from, size);
        Page<Item> itemsPage = itemRepository.findByUserIdOrderByIdAsc(userId, pageRequest);
        return itemsPage.stream()
                .collect(Collectors.toList());
    }

    private Pageable createPageRequestUsing(int page, int size) {
        return PageRequest.of(page, size);
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
        itemRepository.setIdToOne();
    }

    @Override
    public boolean validateIfItemAvailable(int itemId) {
        return itemRepository.findById(itemId).orElseThrow(() -> new ValidationException404("item not found"))
                .getAvailable();
    }

    @Override
    public Item getItemById(int itemId) {
        return itemRepository.findById(itemId).orElseThrow(() -> new ValidationException404("item not found" + itemId));
    }

    @Override
    @Transactional
    public CommentDto saveComment(CommentDto commentDto, int itemId, int userId) {
        List<BookingDtoIdAndBooker> val = bookingService.findAllByBookerAndItemIdAndGoodState(userId, itemId);
        if (val.isEmpty())
            throw new ValidationException400(userId + " did not book item " + itemId + " and can't leave comments");
        if (commentDto.getText().equals("")) throw new ValidationException400("text should not be empty");
        Comment comment = CommentMapper.commentDtoToComment(commentDto);
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ValidationException404("item not found " + itemId));
        comment.setItem(item);
        User user = UserMapper.dtoToUser(userService.getUserById(userId));
        comment.setUser(user);
        comment = commentRepository.save(comment);
        return CommentMapper.commentToCommentDto(comment);
    }

    @Override
    @Transactional
    public void deleteComments() {
        commentRepository.deleteAll();
        commentRepository.setCommentIdToOne();
    }

    @Override
    public List<ItemDto> getItemByRequestId(int requestId) {
        List<Item> allByRequestId = null;
        try {
            allByRequestId = itemRepository.findAllByRequestId(requestId);
        } catch (Exception e) {
            log.debug(e.getMessage() + " items for request " + requestId + " not found");
        }
        return ItemMapper.itemToDtoList(allByRequestId);
    }

    private void tryToAddNextAndLastBooking(int itemId, int userId, ItemDto itemDto) {
        try {
            List<BookingDtoIdAndBooker> nextAndLastBooking = bookingService.findNextAndLastBookingByItemId(itemId, userId, itemDto);
            try {
                itemDto.setLastBooking(nextAndLastBooking.get(1));
            } catch (IndexOutOfBoundsException e) {
                log.debug("last booking for item " + itemId + " cant be vied for user " + userId);
            }
            try {
                itemDto.setNextBooking(nextAndLastBooking.get(0));
            } catch (IndexOutOfBoundsException e) {
                log.debug("last booking for item " + itemId + " cant be vied for user " + userId);
            }
        } catch (NullPointerException e) {
            log.debug("Could not load next booking.");
        }
    }
}
