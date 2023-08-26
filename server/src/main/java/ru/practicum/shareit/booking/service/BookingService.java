package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDtoIdAndBooker;
import ru.practicum.shareit.booking.dto.BookingDtoWithStartEndItemId;
import ru.practicum.shareit.booking.dto.BookingWithItemDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface BookingService {
    BookingWithItemDto save(BookingDtoWithStartEndItemId bookingDtoWithStartEndItemId, int userId);

    void deleteAll();

    BookingWithItemDto approveBooking(int bookingId, boolean accept, int userId);

    BookingWithItemDto findById(int bookingId, int userId);

    List<BookingWithItemDto> findAllByBookerId(int userId, String status, Integer size, Integer from);

    List<BookingWithItemDto> findAllByOwnerId(int userId, String status, Integer size, Integer from);

    List<BookingDtoIdAndBooker> findNextAndLastBookingByItemId(int itemId, int userId, ItemDto itemDto);

    List<BookingDtoIdAndBooker> findAllByBookerAndItemIdAndGoodState(int userId, int itemId);
}
