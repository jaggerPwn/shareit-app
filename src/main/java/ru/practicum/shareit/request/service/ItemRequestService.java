package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDto saveRequest(ItemRequestDto itemRequestDto, int userId);

    List<ItemRequestDto> getAllRequestsOfRequestor(int userId);

    List<ItemRequestDto> getRequestPage(int userId, Integer size, Integer from);
}
