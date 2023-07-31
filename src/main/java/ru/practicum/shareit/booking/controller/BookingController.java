package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingWithItemDto;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingWithItemDto saveBooking(@Valid @RequestBody BookingWithItemDto bookingWithItemDto, @RequestHeader("X-Sharer-User-Id") int userId) {
        return bookingService.save(bookingWithItemDto, userId);
    }

    @PatchMapping("/{id}")
    public BookingWithItemDto ApproveBooking(@PathVariable(value = "id") int bookingId, @RequestParam("approved") boolean accept,
                                             @RequestHeader("X-Sharer-User-Id") int userId) {
        return bookingService.approveBooking(bookingId, accept, userId);
    }

    @GetMapping("/{id}")
    public BookingWithItemDto findBookingById(@PathVariable(value = "id") int bookingId,
                                              @RequestHeader("X-Sharer-User-Id") int userId) {
        return bookingService.findById(bookingId, userId);
    }

    @GetMapping
    public List<BookingWithItemDto> findAllBookingByBookerIdAndState(
            @RequestParam(name = "state", defaultValue = "ALL", required = false) String status,
            @RequestHeader("X-Sharer-User-Id") int userId) {
        return bookingService.findAllByBookerId(userId, status);
    }


    @GetMapping("/owner")
    public List<BookingWithItemDto> findAllBookingsByOwnerIdAndState(
            @RequestParam(name = "state", defaultValue = "ALL", required = false) String status,
            @RequestHeader("X-Sharer-User-Id") int userId) {
        return bookingService.findAllByOwnerId(userId, status);
    }

    @DeleteMapping
    private void deleteAll() {
        bookingService.deleteAll();
    }
}
