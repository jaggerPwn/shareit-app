package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.model.Booking;

import java.util.List;
import java.util.stream.Collectors;

public class BookingMapper {

    public static BookingDto bookingToDto(Booking booking) {
        return BookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(booking.getStatus())
                .booker(booking.getUser())
                .itemId(booking.getItem().getId())
                .build();
    }

    public static Booking dtoToBooking(BookingDto bookingDto) {
        return Booking.builder()
                .id(bookingDto.getId())
                .start(bookingDto.getStart())
                .end(bookingDto.getEnd())
                .status(bookingDto.getStatus())
                .build();
    }

    public static List<BookingDto> bookingToDtoList(List<Booking> bookings) {
        return bookings.stream()
                .map(BookingMapper::bookingToDto)
                .collect(Collectors.toList());
    }

    public static List<Booking> dtoToBookingList(List<BookingDto> bookingDtos) {
        return bookingDtos.stream()
                .map(BookingMapper::dtoToBooking)
                .collect(Collectors.toList());
    }
}
