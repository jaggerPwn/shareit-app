package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.model.Booking;

import java.util.List;
import java.util.stream.Collectors;

public class BookingMapper {
    public static Booking bookingDtoWithStartEndItemIdTOBooking(
            BookingDtoWithStartEndItemId bookingDtoWithStartEndItemId) {
        return Booking.builder()
                .id(bookingDtoWithStartEndItemId.getId())
                .start(bookingDtoWithStartEndItemId.getStart())
                .end(bookingDtoWithStartEndItemId.getEnd())
                .build();
    }

    public static BookingDtoIdAndBooker bookingToDtoIdAndBooker(Booking booking) {
        return BookingDtoIdAndBooker.builder()
                .id(booking.getId())
                .bookerId(booking.getUser().getId())
                .build();
    }

    public static List<BookingDtoIdAndBooker> bookingToDtoListIdAndBooker(List<Booking> bookings) {
        return bookings.stream()
                .map(BookingMapper::bookingToDtoIdAndBooker)
                .collect(Collectors.toList());
    }


    public static List<Booking> dtoToBookingList(List<BookingWithItemDto> bookingDtoIdAndBookers) {
        return bookingDtoIdAndBookers.stream()
                .map(BookingMapper::dtoWithItemToBooking)
                .collect(Collectors.toList());
    }

    public static List<BookingWithItemDto> bookingToDtoList(List<Booking> bookings) {
        return bookings.stream()
                .map(BookingMapper::bookingToDtoWithItem)
                .collect(Collectors.toList());
    }

    public static BookingWithItemDto bookingToDtoWithItem(Booking booking) {
        return BookingWithItemDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(booking.getStatus())
                .booker(booking.getUser())
                .item(booking.getItem())
                .build();
    }

    public static Booking dtoWithItemToBooking(BookingWithItemDto bookingWithItemDto) {
        return Booking.builder()
                .id(bookingWithItemDto.getId())
                .user(bookingWithItemDto.getBooker())
                .item(bookingWithItemDto.getItem())
                .start(bookingWithItemDto.getStart())
                .end(bookingWithItemDto.getEnd())
                .status(bookingWithItemDto.getStatus())
                .build();
    }

    public static List<BookingDtoIdAndBooker> listBookingToDtoIdAndBooker(List<Booking> allByBookerAndItemId) {
        return allByBookerAndItemId.stream()
                .map(BookingMapper::bookingToDtoIdAndBooker)
                .collect(Collectors.toList());
    }
}
