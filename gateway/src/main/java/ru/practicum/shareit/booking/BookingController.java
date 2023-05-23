package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingRequest;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.exception.model.MyValidationException;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@Slf4j
@Validated
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> save(@RequestHeader("X-Sharer-User-Id") long userId,
                                       @RequestBody @Valid BookingRequest bookingRequest) {
        log.info("Create booking {} by userId={}", bookingRequest, userId);
        return bookingClient.save(userId, bookingRequest);
    }

    @PatchMapping("{bookingId}")
    public ResponseEntity<Object> update(@PathVariable Long bookingId,
                                         @RequestHeader("X-Sharer-User-Id") long userId,
                                         @RequestParam(name = "approved") boolean isApprove) {
        log.info("Update bookingId={}", bookingId);
        return bookingClient.update(bookingId, userId, isApprove);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> findById(@RequestHeader("X-Sharer-User-Id") long userId,
                                           @PathVariable Long bookingId) {
        log.info("Find booking {}, userId={}", bookingId, userId);
        return bookingClient.findById(bookingId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> findAllByState(@RequestHeader("X-Sharer-User-Id") long userId,
                                                 @RequestParam(name = "state", defaultValue = "all") String state,
                                                 @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                 @Positive @RequestParam(defaultValue = "10") Integer size) {
        BookingState bookingState = BookingState.from(state)
                .orElseThrow(() -> new MyValidationException("Unknown state: " + state));
        log.info("Find booking with bookingState {}, userId={}, from={}, size={}", state, userId, from, size);
        return bookingClient.findAllByState(userId, bookingState, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> findAllByOwner(@RequestHeader("X-Sharer-User-Id") long userId,
                                                 @RequestParam(defaultValue = "ALL") String state,
                                                 @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                 @Positive @RequestParam(defaultValue = "10") Integer size) {
        BookingState bookingState = BookingState.from(state)
                .orElseThrow(() -> new MyValidationException("Unknown state: " + state));
        log.info("Find all bookings by userId={}", userId);
        return bookingClient.findAllByOwner(userId, bookingState, from, size);
    }
}
