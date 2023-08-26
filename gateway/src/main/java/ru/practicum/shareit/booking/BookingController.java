package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDtoWithStartEndItemId;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    @Validated
    @PostMapping
    public ResponseEntity<Object> saveBooking(
            @Valid @RequestBody BookingDtoWithStartEndItemId bookingDtoWithStartEndItemId,
            @RequestHeader("X-Sharer-User-Id") int userId) {
        if (bookingDtoWithStartEndItemId.getStart() == null || bookingDtoWithStartEndItemId.getEnd() == null)
            throw new IllegalArgumentException("Incorrect date of booking");
        if (bookingDtoWithStartEndItemId.getStart().isAfter(bookingDtoWithStartEndItemId.getEnd()))
            throw new IllegalArgumentException("Incorrect date of booking");
        return bookingClient.saveItem(userId, bookingDtoWithStartEndItemId);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> ApproveBooking(
            @PathVariable(value = "id") int bookingId,
            @RequestParam("approved") boolean accept,
            @RequestHeader("X-Sharer-User-Id") int userId) {
        return bookingClient.approveBooking(bookingId, accept, userId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> findBookingById(
            @PathVariable(value = "id") Integer bookingId,
            @RequestHeader("X-Sharer-User-Id") Integer userId) {
        return bookingClient.findBookingById(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> findAllBookingByBookerIdAndState(
            @RequestHeader("X-Sharer-User-Id") Integer userId,
            @RequestParam(name = "state", defaultValue = "ALL", required = false) String state,
            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0", required = false) Integer from,
            @Positive @RequestParam(name = "size", defaultValue = "10", required = false) Integer size) {
        return bookingClient.findAllBookingByBookerIdAndState(userId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> findAllBookingsByOwnerIdAndState(
            @RequestHeader("X-Sharer-User-Id") Integer userId,
            @RequestParam(name = "state", defaultValue = "ALL", required = false) String state,
            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0", required = false) Integer from,
            @Positive @RequestParam(name = "size", defaultValue = "10", required = false) Integer size) {
        return bookingClient.findAllByOwnerId(userId, state, from, size);
    }

    @DeleteMapping
    private void deleteAll() {
        bookingClient.deleteAll();
    }
}
