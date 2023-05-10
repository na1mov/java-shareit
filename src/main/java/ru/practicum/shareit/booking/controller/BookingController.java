package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequest;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import java.util.List;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingDto save(@RequestBody @Valid BookingRequest bookingDto,
                           @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingService.save(bookingDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto update(@PathVariable Long bookingId, @RequestParam Boolean approved,
                             @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingService.update(bookingId, userId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto findById(@PathVariable Long bookingId, @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingService.findByIdAndUserId(bookingId, userId);
    }

    @GetMapping
    public List<BookingDto> findAllByState(@RequestParam(defaultValue = "ALL") String state,
                                           @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingService.findAllByState(state, userId);
    }

    @GetMapping("/owner")
    public List<BookingDto> findAllByOwner(@RequestParam(defaultValue = "ALL") String state,
                                           @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingService.findAllByOwner(state, userId);
    }
}
