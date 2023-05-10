package ru.practicum.shareit.booking.mapper;

import ru.practicum.shareit.booking.dto.BookingDtoShort;
import ru.practicum.shareit.booking.model.Booking;

public class BookingMapperShort {
    public static BookingDtoShort bookingToBookingDtoShort(Booking booking) {
        return BookingDtoShort.builder()
                .id(booking.getId())
                .bookerId(booking.getBooker().getId())
                .build();
    }
}
