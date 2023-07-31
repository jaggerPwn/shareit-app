package ru.practicum.shareit.booking.validation;

import ru.practicum.shareit.booking.dto.BookingWithItemDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.ValidationException400;
import ru.practicum.shareit.exception.ValidationException404;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;

public class BookingValidation {

    public static void validateBooking(BookingWithItemDto bookingWithItemDto, ItemService itemService) {
        if (bookingWithItemDto.getEnd() == null || bookingWithItemDto.getStart() == null)
            throw new ValidationException400("item " + bookingWithItemDto.getItem().getId() + " has null dates");

        if (bookingWithItemDto.getStart().isBefore(LocalDateTime.now()))
            throw new ValidationException400("Start date " + bookingWithItemDto.getStart() + " cant be  in past");

        if (!itemService.validateIfItemAvailable(bookingWithItemDto.getItem().getId()))
            throw new ValidationException400("item " + bookingWithItemDto.getItem().getId() + " is unavailable");

        if (bookingWithItemDto.getEnd().isBefore(bookingWithItemDto.getStart()))
            throw new ValidationException400("end: " +
                    bookingWithItemDto.getEnd() + "is before start: " + bookingWithItemDto.getStart());

        if (bookingWithItemDto.getEnd().isEqual(bookingWithItemDto.getStart()))
            throw new ValidationException400("end: " +
                    bookingWithItemDto.getEnd() + "is equal start: " + bookingWithItemDto.getStart());
    }

    public static void validateUserAuthorization(int bookingId, int userId,
                                                 BookingRepository bookingRepository) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ValidationException404("Booking not found " + bookingId));
        if (booking
                .getItem()
                .getUser()
                .getId() != userId)
            if (booking
                    .getUser()
                    .getId() != userId) {
                throw new ValidationException404("User " + userId + " is not authorized to this booking");
            }
    }

    public static void validateIfUserExists(int userId, UserService userService) {
        if (userService.getUserById(userId) == null) throw new ValidationException404("User " + userId +
                " does not exists");
    }

    public static void validateIfUserBooksHisItems(int userId, ItemService itemService,
                                                   BookingWithItemDto bookingWithItemDto) {
        if (itemService.getItemOwner(bookingWithItemDto.getItem().getId()).getId() == userId)
            throw new ValidationException404("not authorized");
    }
}
