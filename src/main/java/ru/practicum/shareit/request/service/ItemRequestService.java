package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDto saveRequest(ItemRequestDto itemRequestDto, int userId);

    List<ItemRequestDto> getRequestsOfRequestor(int userId);

    List<ItemRequestDto> getAllRequestPage(int userId, Integer size, Integer from);

    ItemRequestDto getRequestById(int id, int userId);

    void deleteAll();
}
