package ru.practicum.shareit.booking.dto;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.user.dto.UserDto;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
class BookingDtoShortTest {
    @Autowired
    JacksonTester<BookingDtoShort> json;

    @SneakyThrows
    @Test
    void bookingDtoShortTest() {
        BookingDtoShort bookingDtoShort = BookingDtoShort.builder()
                .id(1L)
                .bookerId(1L)
                .build();
        JsonContent<BookingDtoShort> jsonTest = json.write(bookingDtoShort);

        assertThat(jsonTest).extractingJsonPathNumberValue("$.id")
                .isEqualTo(bookingDtoShort.getId().intValue());
        assertThat(jsonTest).extractingJsonPathNumberValue("$.bookerId")
                .isEqualTo(bookingDtoShort.getBookerId().intValue());
    }
}