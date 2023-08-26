package ru.practicum.shareit.booking.validation;

import ru.practicum.shareit.booking.dto.BookingDtoWithStartEndItemId;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.ValidationException400;
import ru.practicum.shareit.exception.ValidationException404;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;

public class BookingValidation {

    public static void validateBooking(Booking booking, ItemService itemService) {

        if (booking.getStart().isBefore(LocalDateTime.now()))
            throw new ValidationException400("Start date " + booking.getStart() + " cant be  in past");

        if (!itemService.validateIfItemAvailable(booking.getItem().getId()))
            throw new ValidationException400("item " + booking.getItem().getId() + " is unavailable");

        if (booking.getEnd().isBefore(booking.getStart()))
            throw new ValidationException400("end: " +
                    booking.getEnd() + "is before start: " + booking.getStart());

        if (booking.getEnd().isEqual(booking.getStart()))
            throw new ValidationException400("end: " +
                    booking.getEnd() + "is equal start: " + booking.getStart());
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
                                                   Booking booking) {
        if (itemService.getItemById(booking.getItem().getId()).getId() == userId)
            throw new ValidationException404("not authorized");
    }

    public static void validateBookingStartEnd(BookingDtoWithStartEndItemId bookingDtoWithStartEndItemId) {
        if (bookingDtoWithStartEndItemId.getStart() == null || bookingDtoWithStartEndItemId.getEnd() == null) {
            throw new ValidationException400("Booking must have both dates");
        }
    }
//not working in TZ14 for existing POSTMAN tests
//    public static void validateForIntersection(Booking booking, BookingRepository bookingRepository) {
//        Booking intersections = bookingRepository.findIntersections(booking.getStart(), booking.getEnd());
//        if (intersections != null)
//            throw new ValidationException400
//                    ("found intersections from " + booking.getStart() + " to " + booking.getEnd());
//    }
}
