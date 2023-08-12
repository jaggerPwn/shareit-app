package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserService userService;
    private final ItemService itemService;

    @Override
    public ItemRequestDto saveRequest(ItemRequestDto itemRequestDto, int userId) {
        User user = UserMapper.dtoToUser(userService.getUserById(userId));
        ItemRequest itemRequest = ItemRequestMapper.itemRequestDtoToItemRequest(itemRequestDto);
        itemRequest.setUser(user);
        return ItemRequestMapper.itemRequestToItemRequestDto(itemRequestRepository.save(itemRequest));
    }
    @Override
    public List<ItemRequestDto> getAllRequestsOfRequestor(int userId) {
        UserValidator.validateIfUserExists(userId, userService);
        List<ItemRequest> itemsRequestList = itemRequestRepository.findByUserIdOrderByIdAsc(userId);
        List<ItemRequestDto> itemRequestDTOList = ItemRequestMapper.listItemRequestToItemRequestDto(itemsRequestList);
        for (ItemRequestDto itemRequestDto : itemRequestDTOList) {
            itemRequestDto.setItems(itemService.getItemByRequestId(itemRequestDto.getId()));
        }
        return itemRequestDTOList;
    }
    @Override
    public List<ItemRequestDto> getRequestPage(int userId, Integer size, Integer from) {
        UserValidator.validateIfUserExists(userId, userService);
        List<ItemRequest> itemsRequestList = new ArrayList<>();
        if (size != null && from != null) {
            itemsRequestList = getItemsRequestsAsPage(userId, size, from);
        } else {
            throw new ValidationException404("property 'from' not found");
        }
        List<ItemRequestDto> itemRequestDTOList = ItemRequestMapper.listItemRequestToItemRequestDto(itemsRequestList);
        for (ItemRequestDto itemRequestDto : itemRequestDTOList) {
            itemRequestDto.setItems(itemService.getItemByRequestId(itemRequestDto.getId()));
        }
        return itemRequestDTOList;
    }

    private Pageable createPageRequestUsing(int page, int size) {
        return PageRequest.of(page, size);
    }

    @NotNull
    private List<ItemRequest> getItemsRequestsAsPage(int userId, Integer size, Integer from) {
        Pageable pageRequest = createPageRequestUsing(from, size);
        Page<ItemRequest> itemsRequestPage = itemRequestRepository.findByUserIdOrderByIdAsc(userId, pageRequest);
        return itemsRequestPage.stream()
                .collect(Collectors.toList());
    }

}
