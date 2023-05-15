package ru.practicum.shareit.booking.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingDtoShort;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class BookingMapperShortTest {
    @Test
    void bookingToBookingDtoShort() {
        User userOne = new User(1L, "testNameOne", "testEmailOne@gmail.com");
        User userTwo = new User(2L, "testNameTwo", "testEmailTwo@gmail.com");
        Item item = new Item(1L, "itemName", "itemDescription", true, userOne, null);
        Booking booking = Booking.builder()
                .id(item.getId())
                .start(LocalDateTime.now().plusMinutes(8))
                .end(LocalDateTime.now().plusMinutes(16))
                .item(item)
                .booker(userTwo)
                .status(BookingStatus.WAITING)
                .build();

        BookingDtoShort bookingDtoShort = BookingMapperShort.bookingToBookingDtoShort(booking);

        assertEquals(booking.getId(), bookingDtoShort.getId());
        assertEquals(booking.getBooker().getId(), bookingDtoShort.getBookerId());
    }
}