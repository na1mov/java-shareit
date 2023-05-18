package ru.practicum.shareit.booking.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequest;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.model.MyValidationException;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static ru.practicum.shareit.booking.model.BookingStatus.*;

@Slf4j
@Service
@AllArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final UserMapper userMapper;
    private final BookingMapper bookingMapper;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public BookingDto save(BookingRequest bookingRequest, Long bookerId) {
        log.info("Сохранение бронирования.");
        User booker = userRepository.findById(bookerId).orElseThrow(() -> new NotFoundException(bookerId.toString()));
        Item item = itemRepository.findById(bookingRequest.getItemId())
                .orElseThrow(() -> new NotFoundException(bookingRequest.getItemId().toString()));
        if (Objects.equals(item.getOwner().getId(), bookerId)) {
            throw new NotFoundException("Нельзя взять в аренду свою вещь.");
        }
        if (!bookingRequest.getStart().isBefore(bookingRequest.getEnd())) {
            throw new MyValidationException("Ошибка в датах бронирования.");
        }
        if (!item.getAvailable()) {
            throw new MyValidationException(String.format("В данный момент вещь с ID:%d недоступна", item.getId()));
        }
        Booking booking = bookingMapper.bookingRequestToBooking(bookingRequest);
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStatus(WAITING);
        return bookingMapper.bookingToBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto update(Long bookingId, Long userId, Boolean isApprove) {
        log.info(String.format("Обновление брони c ID:%d", bookingId));
        Booking booking = findById(bookingId);
        if (!Objects.equals(booking.getItem().getOwner().getId(), userId)) {
            throw new NotFoundException(
                    String.format("Ошибка доступа. Изменить статус вещи с ID:%d может только её владелец.", bookingId));
        }
        if (booking.getStatus().equals(WAITING)) {
            booking.setStatus(isApprove ? APPROVED : REJECTED);
        } else {
            throw new MyValidationException("Ошибка изменения статуса.");
        }
        return bookingMapper.bookingToBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto findByIdAndUserId(Long bookingId, Long userId) {
        log.info(String.format("Поиск брони с ID:%d", bookingId));
        Booking booking = findById(bookingId);
        if (!Objects.equals(booking.getItem().getOwner().getId(), userId)
                && !Objects.equals(userId, booking.getBooker().getId())) {
            throw new NotFoundException(
                    String.format("Ошибка доступа. Изменить статус вещи с ID:%d может только её владелец.", bookingId));
        }
        return bookingMapper.bookingToBookingDto(booking);
    }

    @Override
    public List<BookingDto> findAllByState(String state, Long userId, Pageable pageable) {
        log.info(String.format("Поиск списка брони для пользователя с ID:%d по статусу:%s", userId, state));
        userMapper.userDtoToUser(userService.findById(userId));
        checkValidState(state);
        switch (BookingState.valueOf(state)) {
            case ALL:
                return bookingRepository.findAllByBookerIdOrderByStartDesc(userId, pageable)
                        .stream()
                        .map(bookingMapper::bookingToBookingDto)
                        .collect(Collectors.toList());
            case CURRENT:
                return bookingRepository.findAllByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(
                                userId, LocalDateTime.now(), LocalDateTime.now(), pageable)
                        .stream()
                        .map(bookingMapper::bookingToBookingDto)
                        .collect(Collectors.toList());
            case PAST:
                return bookingRepository
                        .findAllByBookerIdAndEndIsBeforeOrderByStartDesc(userId, LocalDateTime.now(), pageable)
                        .stream()
                        .map(bookingMapper::bookingToBookingDto)
                        .collect(Collectors.toList());
            case FUTURE:
                return bookingRepository
                        .findAllByBookerIdAndStartIsAfterOrderByStartDesc(userId, LocalDateTime.now(), pageable)
                        .stream()
                        .map(bookingMapper::bookingToBookingDto)
                        .collect(Collectors.toList());
            case WAITING:
                return bookingRepository
                        .findAllByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING, pageable)
                        .stream()
                        .map(bookingMapper::bookingToBookingDto)
                        .collect(Collectors.toList());
            case REJECTED:
                return bookingRepository
                        .findAllByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED, pageable)
                        .stream()
                        .map(bookingMapper::bookingToBookingDto)
                        .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    @Override
    public List<BookingDto> findAllByOwner(String state, Long userId, Pageable pageable) {
        log.info(String.format("Поиск списка брони для пользователя с ID:%d по статусу:%s", userId, state));
        userMapper.userDtoToUser(userService.findById(userId));
        checkValidState(state);
        switch (BookingState.valueOf(state)) {
            case ALL:
                return bookingRepository.findAllByItemOwnerIdOrderByStartDesc(userId, pageable)
                        .stream()
                        .map(bookingMapper::bookingToBookingDto)
                        .collect(Collectors.toList());
            case CURRENT:
                return bookingRepository.findAllByItemOwnerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(userId,
                                LocalDateTime.now(), LocalDateTime.now(), pageable)
                        .stream()
                        .map(bookingMapper::bookingToBookingDto)
                        .collect(Collectors.toList());
            case PAST:
                return bookingRepository
                        .findAllByItemOwnerIdAndEndIsBeforeOrderByStartDesc(userId, LocalDateTime.now(), pageable)
                        .stream()
                        .map(bookingMapper::bookingToBookingDto)
                        .collect(Collectors.toList());
            case FUTURE:
                return bookingRepository
                        .findAllByItemOwnerIdAndStartIsAfterOrderByStartDesc(userId, LocalDateTime.now(), pageable)
                        .stream()
                        .map(bookingMapper::bookingToBookingDto)
                        .collect(Collectors.toList());
            case WAITING:
                return bookingRepository
                        .findAllByItemOwnerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING, pageable)
                        .stream()
                        .map(bookingMapper::bookingToBookingDto)
                        .collect(Collectors.toList());
            case REJECTED:
                return bookingRepository
                        .findAllByItemOwnerIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED, pageable)
                        .stream()
                        .map(bookingMapper::bookingToBookingDto)
                        .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    private Booking findById(Long bookingId) {
        return bookingRepository.findById(bookingId).orElseThrow(() ->
                new NotFoundException(String.format("Бронь с ID:%d отсутствует в базе", bookingId)));
    }

    private void checkValidState(String state) {
        try {
            BookingState.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new MyValidationException(String.format("Unknown state: %s", state));
        }
    }
}
