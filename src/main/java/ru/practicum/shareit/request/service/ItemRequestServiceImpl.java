package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ValidationException400;
import ru.practicum.shareit.exception.ValidationException404;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.validation.UserValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserService userService;
    private final ItemService itemService;

    @Override
    @Transactional
    public ItemRequestDto saveRequest(ItemRequestDto itemRequestDto, int userId) {
        User user = UserMapper.dtoToUser(userService.getUserById(userId));

        if (itemRequestDto.getDescription() == null) throw new ValidationException400("description should not be null");

        ItemRequest itemRequest = ItemRequestMapper.itemRequestDtoToItemRequest(itemRequestDto);
        itemRequest.setUser(user);
        ItemRequest savedRequest = itemRequestRepository.save(itemRequest);
        return ItemRequestMapper.itemRequestToItemRequestDto(savedRequest);
    }

    @Override
    public List<ItemRequestDto> getRequestsOfRequestor(int userId) {
        UserValidator.validateIfUserExists(userId, userService);
        List<ItemRequest> itemsRequestList = itemRequestRepository.findAllByUserIdOrderByIdAsc(userId);
        List<ItemRequestDto> itemRequestDTOList = ItemRequestMapper.listItemRequestToItemRequestDto(itemsRequestList);
        for (ItemRequestDto itemRequestDto : itemRequestDTOList) {
            itemRequestDto.setItems(itemService.getItemByRequestId(itemRequestDto.getId()));
        }
        return itemRequestDTOList;
    }

    @Override
    public List<ItemRequestDto> getAllRequestPage(int userId, Integer size, Integer from) {
        UserValidator.validateIfUserExists(userId, userService);
        List<ItemRequest> itemsRequestList = new ArrayList<>();
        if (size != null && from != null) {
            itemsRequestList = getItemsRequestsAsPage(userId, size, from);
        } else {
            itemsRequestList = itemRequestRepository.findAllByUserId(userId);
        }
        List<ItemRequestDto> itemRequestDTOList = ItemRequestMapper.listItemRequestToItemRequestDto(itemsRequestList);
        for (ItemRequestDto itemRequestDto : itemRequestDTOList) {
            itemRequestDto.setItems(itemService.getItemByRequestId(itemRequestDto.getId()));
        }
        return itemRequestDTOList;
    }

    @Override
    public ItemRequestDto getRequestById(int id, int userId) {
        UserValidator.validateIfUserExists(userId, userService);
        Optional<ItemRequest> itemRequestOptional = itemRequestRepository.findById(id);
        ItemRequest itemRequest = itemRequestOptional.orElseThrow(() -> new ValidationException404("request " + id + " not found"));
        ItemRequestDto itemRequestDto = ItemRequestMapper.itemRequestToItemRequestDto(itemRequest);
        itemRequestDto.setItems(itemService.getItemByRequestId(itemRequestDto.getId()));
        return itemRequestDto;
    }

    @Override
    @Transactional
    public void deleteAll() {
        itemRequestRepository.deleteAll();
        itemRequestRepository.alterTableSetFromBeginig();
    }

    private Pageable createPageRequestUsing(int page, int size) {
        return PageRequest.of(page, size);
    }

    @NotNull
    private List<ItemRequest> getItemsRequestsAsPage(int userId, Integer size, Integer from) {
        Pageable pageRequest = createPageRequestUsing(from, size);
        Page<ItemRequest> itemsRequestPage = itemRequestRepository.findAllByUserIdNotOrderByIdAsc(userId, pageRequest);
        return itemsRequestPage.stream()
                .collect(Collectors.toList());
    }

}
