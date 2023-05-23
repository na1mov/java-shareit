package ru.practicum.shareit.booking.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequest;

import java.util.List;

public interface BookingService {
    BookingDto save(BookingRequest bookingDto, Long userId);

    BookingDto update(Long bookingId, Long userId, Boolean approved);

    BookingDto findByIdAndUserId(Long bookingId, Long userId);

    List<BookingDto> findAllByState(String state, Long userId, Pageable pageable);

    List<BookingDto> findAllByOwner(String state, Long userId, Pageable pageable);
}
