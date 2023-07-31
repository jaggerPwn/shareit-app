package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDtoIdAndBooker;
import ru.practicum.shareit.booking.dto.BookingWithItemDto;

import java.util.List;

public interface BookingService {
    BookingWithItemDto save(BookingWithItemDto bookingWithItemDto, int userId);

    void deleteAll();

    BookingWithItemDto approveBooking(int bookingId, boolean accept, int userId);

    BookingWithItemDto findById(int bookingId, int userId);
    List<BookingWithItemDto> findAllByBookerId(int userId, String status);

    List<BookingWithItemDto> findAllByOwnerId(int userId, String status);

    BookingDtoIdAndBooker findNextBookingByItemId(int itemId);
}
