package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingDtoIdAndBooker;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemDto {
    private int id;
    private String name;
    private String description;
    private Boolean available;
    private BookingDtoIdAndBooker lastBooking;
    private BookingDtoIdAndBooker nextBooking;
    private List<CommentDto> comments;
    private Integer requestId;
}
