package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingDtoShort;

import java.util.List;

@Data
@Builder
public class ItemDtoEnhanced {
    Long id;
    String name;
    String description;
    Boolean available;
    BookingDtoShort lastBooking;
    BookingDtoShort nextBooking;
    List<CommentDto> comments;
}
