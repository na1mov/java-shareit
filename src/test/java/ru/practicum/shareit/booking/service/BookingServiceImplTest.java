package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequest;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
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
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;


class BookingServiceImplTest {
    @Mock
    BookingRepository bookingRepository;
    @Mock
    UserService userService;
    @Mock
    UserMapper userMapper;
    @Mock
    BookingMapper bookingMapper;
    @Mock
    UserRepository userRepository;
    @Mock
    ItemRepository itemRepository;
    BookingService bookingService;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    @BeforeEach
    void beforeEach() {
        bookingRepository = Mockito.mock(BookingRepository.class);
        userService = Mockito.mock(UserService.class);
        userMapper = Mappers.getMapper(UserMapper.class);
        bookingMapper = Mappers.getMapper(BookingMapper.class);
        userRepository = Mockito.mock(UserRepository.class);
        itemRepository = Mockito.mock(ItemRepository.class);
        bookingService = new BookingServiceImpl(bookingRepository,
                userService,
                userMapper,
                bookingMapper,
                userRepository,
                itemRepository);
    }

    @Test
    void save() {
        User userOne = new User(1L, "testNameOne", "testEmailOne@gmail.com");
        User userTwo = new User(2L, "testNameTwo", "testEmailTwo@gmail.com");
        Item item = new Item(1L, "itemName", "itemDescription", true, userOne, null);
        BookingRequest bookingRequest = BookingRequest.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now().plusMinutes(8))
                .end(LocalDateTime.now().plusMinutes(16))
                .build();
        Booking booking = Booking.builder()
                .id(item.getId())
                .start(LocalDateTime.now().plusMinutes(8))
                .end(LocalDateTime.now().plusMinutes(16))
                .item(item)
                .booker(userTwo)
                .status(BookingStatus.WAITING)
                .build();

        bookingMapper.bookingToBookingDto(null);
        when(userRepository.findById(any())).thenReturn(Optional.of(userTwo));
        when(itemRepository.findById(any())).thenReturn(Optional.of(item));
        when(bookingRepository.save(any())).thenReturn(booking);
        BookingDto bookingDto = bookingService.save(bookingRequest, userTwo.getId());

        assertEquals(bookingRequest.getItemId(), bookingDto.getItem().getId());
        assertEquals(bookingRequest.getEnd().format(formatter), bookingDto.getEnd().format(formatter));
        assertEquals(bookingRequest.getStart().format(formatter), bookingDto.getStart().format(formatter));
    }

    @Test
    void update() {
        User userOne = new User(1L, "testNameOne", "testEmailOne@gmail.com");
        User userTwo = new User(2L, "testNameTwo", "testEmailTwo@gmail.com");
        Item item = new Item(1L, "itemName", "itemDescription", true, userOne, null);
        BookingRequest bookingRequest = BookingRequest.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now().plusMinutes(8))
                .end(LocalDateTime.now().plusMinutes(16))
                .build();
        Booking booking = Booking.builder()
                .id(item.getId())
                .start(LocalDateTime.now().plusMinutes(8))
                .end(LocalDateTime.now().plusMinutes(16))
                .item(item)
                .booker(userTwo)
                .status(BookingStatus.WAITING)
                .build();

        when(bookingRepository.findById(any())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any())).thenReturn(booking);
        BookingDto bookingDto = bookingService.update(1L, userOne.getId(), true);

        assertEquals(bookingRequest.getStart().format(formatter), bookingDto.getStart().format(formatter));
        assertEquals(bookingRequest.getEnd().format(formatter), bookingDto.getEnd().format(formatter));
        assertEquals(bookingRequest.getItemId(), bookingDto.getItem().getId());
        assertEquals(BookingStatus.APPROVED, bookingDto.getStatus());
    }

    @Test
    void updateThrowsMyValidationIfWrongStatus() {
        User userOne = new User(1L, "testNameOne", "testEmailOne@gmail.com");
        User userTwo = new User(2L, "testNameTwo", "testEmailTwo@gmail.com");
        Item item = new Item(1L, "itemName", "itemDescription", true, userOne, null);
        Booking booking = Booking.builder()
                .id(item.getId())
                .start(LocalDateTime.now().plusMinutes(8))
                .end(LocalDateTime.now().plusMinutes(16))
                .item(item)
                .booker(userTwo)
                .status(BookingStatus.REJECTED)
                .build();

        when(bookingRepository.findById(any())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any())).thenReturn(booking);

        assertThrows(MyValidationException.class, () -> bookingService.update(1L, userOne.getId(), true));
    }

    @Test
    void updateThrowsMyValidationIfStatusIsWrong() {
        User userOne = new User(1L, "testNameOne", "testEmailOne@gmail.com");
        User userTwo = new User(2L, "testNameTwo", "testEmailTwo@gmail.com");
        Booking booking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now().plusMinutes(8))
                .end(LocalDateTime.now().plusMinutes(16))
                .status(BookingStatus.REJECTED)
                .booker(userTwo)
                .item(Item.builder()
                        .available(true)
                        .owner(userOne)
                        .build())
                .build();

        when(bookingRepository.findById(any())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any())).thenReturn(booking);

        assertThrows(MyValidationException.class, () -> bookingService.update(1L, 1L, true));
    }

    @Test
    void updateThrowsNotFoundIfUserIsNotOwner() {
        User userTwo = new User(2L, "testNameTwo", "testEmailTwo@gmail.com");
        Booking booking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now().plusMinutes(8))
                .end(LocalDateTime.now().plusMinutes(16))
                .status(BookingStatus.WAITING)
                .booker(userTwo)
                .item(Item.builder()
                        .available(true)
                        .owner(userTwo)
                        .build())
                .build();

        when(bookingRepository.findById(any())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any())).thenReturn(booking);

        assertThrows(NotFoundException.class, () -> bookingService.update(1L, 1L, true));
    }

    @Test
    void findByIdAndUserId() {
        User userOne = new User(1L, "testNameOne", "testEmailOne@gmail.com");
        User userTwo = new User(2L, "testNameTwo", "testEmailTwo@gmail.com");
        Item item = new Item(1L, "itemName", "itemDescription", true, userOne, null);
        BookingRequest bookingRequest = BookingRequest.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now().plusMinutes(8))
                .end(LocalDateTime.now().plusMinutes(16))
                .build();
        Booking booking = Booking.builder()
                .id(item.getId())
                .start(LocalDateTime.now().plusMinutes(8))
                .end(LocalDateTime.now().plusMinutes(16))
                .item(item)
                .booker(userTwo)
                .status(BookingStatus.WAITING)
                .build();

        when(bookingRepository.findById(any())).thenReturn(Optional.of(booking));
        BookingDto bookingDto = bookingService.findByIdAndUserId(booking.getId(), userOne.getId());

        assertEquals(bookingRequest.getItemId(), bookingDto.getItem().getId());
        assertEquals(bookingRequest.getEnd().format(formatter), bookingDto.getEnd().format(formatter));
        assertEquals(bookingRequest.getStart().format(formatter), bookingDto.getStart().format(formatter));
    }

    @Test
    void findByIdAndUserIdWithWrongBookingId() {
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

        when(bookingRepository.findById(-1L)).thenReturn(Optional.ofNullable(booking));

        assertThrows(NotFoundException.class, () -> bookingService.findByIdAndUserId(1L, 1L));
    }

    @Test
    void findByIdAndUserIdWithWrongOwner() {
        User userTwo = new User(2L, "testNameTwo", "testEmailTwo@gmail.com");
        Item item = new Item(1L, "itemName", "itemDescription", true, userTwo, null);
        Booking booking = Booking.builder()
                .id(item.getId())
                .start(LocalDateTime.now().plusMinutes(8))
                .end(LocalDateTime.now().plusMinutes(16))
                .item(item)
                .booker(userTwo)
                .status(BookingStatus.WAITING)
                .build();

        when(bookingRepository.findById(any())).thenReturn(Optional.ofNullable(booking));

        assertThrows(NotFoundException.class, () -> bookingService.findByIdAndUserId(1L, 1L));
    }

    @Test
    void findAllByStateAll() {
        User userOne = new User(1L, "testNameOne", "testEmailOne@gmail.com");
        User userTwo = new User(2L, "testNameTwo", "testEmailTwo@gmail.com");
        Item item = new Item(1L, "itemName", "itemDescription", true, userOne, null);
        BookingRequest bookingRequest = BookingRequest.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now().plusMinutes(8))
                .end(LocalDateTime.now().plusMinutes(16))
                .build();
        Booking booking = Booking.builder()
                .id(item.getId())
                .start(LocalDateTime.now().plusMinutes(8))
                .end(LocalDateTime.now().plusMinutes(16))
                .item(item)
                .booker(userTwo)
                .status(BookingStatus.WAITING)
                .build();

        when(bookingRepository.save(any())).thenReturn(booking);
        when(bookingRepository.findById(any())).thenReturn(Optional.of(booking));
        when(userRepository.findById(any())).thenReturn(Optional.of(userTwo));
        when(bookingService.findAllByState("ALL", userOne.getId(), Pageable.unpaged())).thenReturn(Collections.emptyList());
        BookingDto bookingDto = bookingService.update(userOne.getId(), 1L, true);

        assertEquals(bookingRequest.getStart().format(formatter), bookingDto.getStart().format(formatter));
        assertEquals(bookingRequest.getEnd().format(formatter), bookingDto.getEnd().format(formatter));
        assertEquals(bookingRequest.getItemId(), bookingDto.getItem().getId());
        assertEquals(BookingStatus.APPROVED, bookingDto.getStatus());
    }

    @Test
    void findAllByStateCurrent() {
        User userOne = new User(1L, "testNameOne", "testEmailOne@gmail.com");
        User userTwo = new User(2L, "testNameTwo", "testEmailTwo@gmail.com");
        Item item = new Item(1L, "itemName", "itemDescription", true, userOne, null);
        BookingRequest bookingRequest = BookingRequest.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now().plusMinutes(8))
                .end(LocalDateTime.now().plusMinutes(16))
                .build();
        Booking booking = Booking.builder()
                .id(item.getId())
                .start(LocalDateTime.now().plusMinutes(8))
                .end(LocalDateTime.now().plusMinutes(16))
                .item(item)
                .booker(userTwo)
                .status(BookingStatus.WAITING)
                .build();

        when(bookingRepository.save(any())).thenReturn(booking);
        when(bookingRepository.findById(any())).thenReturn(Optional.of(booking));
        when(userRepository.findById(any())).thenReturn(Optional.of(userTwo));
        when(bookingService.findAllByState("CURRENT", userOne.getId(), Pageable.unpaged())).thenReturn(Collections.emptyList());
        BookingDto bookingDto = bookingService.update(userOne.getId(), 1L, true);

        assertEquals(bookingRequest.getStart().format(formatter), bookingDto.getStart().format(formatter));
        assertEquals(bookingRequest.getEnd().format(formatter), bookingDto.getEnd().format(formatter));
        assertEquals(bookingRequest.getItemId(), bookingDto.getItem().getId());
        assertEquals(BookingStatus.APPROVED, bookingDto.getStatus());
    }

    @Test
    void findAllByStatePast() {
        User userOne = new User(1L, "testNameOne", "testEmailOne@gmail.com");
        User userTwo = new User(2L, "testNameTwo", "testEmailTwo@gmail.com");
        Item item = new Item(1L, "itemName", "itemDescription", true, userOne, null);
        BookingRequest bookingRequest = BookingRequest.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now().plusMinutes(8))
                .end(LocalDateTime.now().plusMinutes(16))
                .build();
        Booking booking = Booking.builder()
                .id(item.getId())
                .start(LocalDateTime.now().plusMinutes(8))
                .end(LocalDateTime.now().plusMinutes(16))
                .item(item)
                .booker(userTwo)
                .status(BookingStatus.WAITING)
                .build();

        when(bookingRepository.save(any())).thenReturn(booking);
        when(bookingRepository.findById(any())).thenReturn(Optional.of(booking));
        when(userRepository.findById(any())).thenReturn(Optional.of(userTwo));
        when(bookingService.findAllByState("PAST", userOne.getId(), Pageable.unpaged())).thenReturn(Collections.emptyList());
        BookingDto bookingDto = bookingService.update(userOne.getId(), 1L, true);

        assertEquals(bookingRequest.getStart().format(formatter), bookingDto.getStart().format(formatter));
        assertEquals(bookingRequest.getEnd().format(formatter), bookingDto.getEnd().format(formatter));
        assertEquals(bookingRequest.getItemId(), bookingDto.getItem().getId());
        assertEquals(BookingStatus.APPROVED, bookingDto.getStatus());
    }

    @Test
    void findAllByStateFuture() {
        User userOne = new User(1L, "testNameOne", "testEmailOne@gmail.com");
        User userTwo = new User(2L, "testNameTwo", "testEmailTwo@gmail.com");
        Item item = new Item(1L, "itemName", "itemDescription", true, userOne, null);
        BookingRequest bookingRequest = BookingRequest.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now().plusMinutes(8))
                .end(LocalDateTime.now().plusMinutes(16))
                .build();
        Booking booking = Booking.builder()
                .id(item.getId())
                .start(LocalDateTime.now().plusMinutes(8))
                .end(LocalDateTime.now().plusMinutes(16))
                .item(item)
                .booker(userTwo)
                .status(BookingStatus.WAITING)
                .build();

        when(bookingRepository.save(any())).thenReturn(booking);
        when(bookingRepository.findById(any())).thenReturn(Optional.of(booking));
        when(userRepository.findById(any())).thenReturn(Optional.of(userTwo));
        when(bookingService.findAllByState("FUTURE", userOne.getId(), Pageable.unpaged())).thenReturn(Collections.emptyList());
        BookingDto bookingDto = bookingService.update(userOne.getId(), 1L, true);

        assertEquals(bookingRequest.getStart().format(formatter), bookingDto.getStart().format(formatter));
        assertEquals(bookingRequest.getEnd().format(formatter), bookingDto.getEnd().format(formatter));
        assertEquals(bookingRequest.getItemId(), bookingDto.getItem().getId());
        assertEquals(BookingStatus.APPROVED, bookingDto.getStatus());
    }

    @Test
    void findAllByStateWaiting() {
        User userOne = new User(1L, "testNameOne", "testEmailOne@gmail.com");
        User userTwo = new User(2L, "testNameTwo", "testEmailTwo@gmail.com");
        Item item = new Item(1L, "itemName", "itemDescription", true, userOne, null);
        BookingRequest bookingRequest = BookingRequest.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now().plusMinutes(8))
                .end(LocalDateTime.now().plusMinutes(16))
                .build();
        Booking booking = Booking.builder()
                .id(item.getId())
                .start(LocalDateTime.now().plusMinutes(8))
                .end(LocalDateTime.now().plusMinutes(16))
                .item(item)
                .booker(userTwo)
                .status(BookingStatus.WAITING)
                .build();

        when(bookingRepository.save(any())).thenReturn(booking);
        when(bookingRepository.findById(any())).thenReturn(Optional.of(booking));
        when(userRepository.findById(any())).thenReturn(Optional.of(userTwo));
        when(bookingService.findAllByState("WAITING", userOne.getId(), Pageable.unpaged())).thenReturn(Collections.emptyList());
        BookingDto bookingDto = bookingService.update(userOne.getId(), 1L, true);

        assertEquals(bookingRequest.getStart().format(formatter), bookingDto.getStart().format(formatter));
        assertEquals(bookingRequest.getEnd().format(formatter), bookingDto.getEnd().format(formatter));
        assertEquals(bookingRequest.getItemId(), bookingDto.getItem().getId());
        assertEquals(BookingStatus.APPROVED, bookingDto.getStatus());
    }

    @Test
    void findAllByStateRejected() {
        User userOne = new User(1L, "testNameOne", "testEmailOne@gmail.com");
        User userTwo = new User(2L, "testNameTwo", "testEmailTwo@gmail.com");
        Item item = new Item(1L, "itemName", "itemDescription", true, userOne, null);
        BookingRequest bookingRequest = BookingRequest.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now().plusMinutes(8))
                .end(LocalDateTime.now().plusMinutes(16))
                .build();
        Booking booking = Booking.builder()
                .id(item.getId())
                .start(LocalDateTime.now().plusMinutes(8))
                .end(LocalDateTime.now().plusMinutes(16))
                .item(item)
                .booker(userTwo)
                .status(BookingStatus.WAITING)
                .build();

        when(bookingRepository.save(any())).thenReturn(booking);
        when(bookingRepository.findById(any())).thenReturn(Optional.of(booking));
        when(userRepository.findById(any())).thenReturn(Optional.of(userTwo));
        when(bookingService.findAllByState("REJECTED", userOne.getId(), Pageable.unpaged())).thenReturn(Collections.emptyList());
        BookingDto bookingDto = bookingService.update(userOne.getId(), 1L, true);

        assertEquals(bookingRequest.getStart().format(formatter), bookingDto.getStart().format(formatter));
        assertEquals(bookingRequest.getEnd().format(formatter), bookingDto.getEnd().format(formatter));
        assertEquals(bookingRequest.getItemId(), bookingDto.getItem().getId());
        assertEquals(BookingStatus.APPROVED, bookingDto.getStatus());
    }

    @Test
    void findAllByOwnerAndStateAll() {
        User userOne = new User(1L, "testNameOne", "testEmailOne@gmail.com");
        User userTwo = new User(2L, "testNameTwo", "testEmailTwo@gmail.com");
        Item item = new Item(1L, "itemName", "itemDescription", true, userOne, null);
        BookingRequest bookingRequest = BookingRequest.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now().plusMinutes(8))
                .end(LocalDateTime.now().plusMinutes(16))
                .build();
        Booking booking = Booking.builder()
                .id(item.getId())
                .start(LocalDateTime.now().plusMinutes(8))
                .end(LocalDateTime.now().plusMinutes(16))
                .item(item)
                .booker(userTwo)
                .status(BookingStatus.WAITING)
                .build();

        when(bookingRepository.save(any())).thenReturn(booking);
        when(bookingRepository.findById(any())).thenReturn(Optional.of(booking));
        when(userRepository.findById(any())).thenReturn(Optional.of(userTwo));
        when(bookingService.findAllByOwner("ALL", userOne.getId(), Pageable.unpaged())).thenReturn(Collections.emptyList());
        BookingDto bookingDto = bookingService.update(userOne.getId(), 1L, true);

        assertEquals(bookingRequest.getStart().format(formatter), bookingDto.getStart().format(formatter));
        assertEquals(bookingRequest.getEnd().format(formatter), bookingDto.getEnd().format(formatter));
        assertEquals(bookingRequest.getItemId(), bookingDto.getItem().getId());
        assertEquals(BookingStatus.APPROVED, bookingDto.getStatus());
    }

    @Test
    void findAllByOwnerAndStateCurrent() {
        User userOne = new User(1L, "testNameOne", "testEmailOne@gmail.com");
        User userTwo = new User(2L, "testNameTwo", "testEmailTwo@gmail.com");
        Item item = new Item(1L, "itemName", "itemDescription", true, userOne, null);
        BookingRequest bookingRequest = BookingRequest.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now().plusMinutes(8))
                .end(LocalDateTime.now().plusMinutes(16))
                .build();
        Booking booking = Booking.builder()
                .id(item.getId())
                .start(LocalDateTime.now().plusMinutes(8))
                .end(LocalDateTime.now().plusMinutes(16))
                .item(item)
                .booker(userTwo)
                .status(BookingStatus.WAITING)
                .build();

        when(bookingRepository.save(any())).thenReturn(booking);
        when(bookingRepository.findById(any())).thenReturn(Optional.of(booking));
        when(userRepository.findById(any())).thenReturn(Optional.of(userTwo));
        when(bookingService.findAllByOwner("CURRENT", userOne.getId(), Pageable.unpaged())).thenReturn(Collections.emptyList());
        BookingDto bookingDto = bookingService.update(userOne.getId(), 1L, true);

        assertEquals(bookingRequest.getStart().format(formatter), bookingDto.getStart().format(formatter));
        assertEquals(bookingRequest.getEnd().format(formatter), bookingDto.getEnd().format(formatter));
        assertEquals(bookingRequest.getItemId(), bookingDto.getItem().getId());
        assertEquals(BookingStatus.APPROVED, bookingDto.getStatus());
    }

    @Test
    void findAllByOwnerAndStatePast() {
        User userOne = new User(1L, "testNameOne", "testEmailOne@gmail.com");
        User userTwo = new User(2L, "testNameTwo", "testEmailTwo@gmail.com");
        Item item = new Item(1L, "itemName", "itemDescription", true, userOne, null);
        BookingRequest bookingRequest = BookingRequest.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now().plusMinutes(8))
                .end(LocalDateTime.now().plusMinutes(16))
                .build();
        Booking booking = Booking.builder()
                .id(item.getId())
                .start(LocalDateTime.now().plusMinutes(8))
                .end(LocalDateTime.now().plusMinutes(16))
                .item(item)
                .booker(userTwo)
                .status(BookingStatus.WAITING)
                .build();

        when(bookingRepository.save(any())).thenReturn(booking);
        when(bookingRepository.findById(any())).thenReturn(Optional.of(booking));
        when(userRepository.findById(any())).thenReturn(Optional.of(userTwo));
        when(bookingService.findAllByOwner("PAST", userOne.getId(), Pageable.unpaged())).thenReturn(Collections.emptyList());
        BookingDto bookingDto = bookingService.update(userOne.getId(), 1L, true);

        assertEquals(bookingRequest.getStart().format(formatter), bookingDto.getStart().format(formatter));
        assertEquals(bookingRequest.getEnd().format(formatter), bookingDto.getEnd().format(formatter));
        assertEquals(bookingRequest.getItemId(), bookingDto.getItem().getId());
        assertEquals(BookingStatus.APPROVED, bookingDto.getStatus());
    }

    @Test
    void findAllByOwnerAndStateFuture() {
        User userOne = new User(1L, "testNameOne", "testEmailOne@gmail.com");
        User userTwo = new User(2L, "testNameTwo", "testEmailTwo@gmail.com");
        Item item = new Item(1L, "itemName", "itemDescription", true, userOne, null);
        BookingRequest bookingRequest = BookingRequest.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now().plusMinutes(8))
                .end(LocalDateTime.now().plusMinutes(16))
                .build();
        Booking booking = Booking.builder()
                .id(item.getId())
                .start(LocalDateTime.now().plusMinutes(8))
                .end(LocalDateTime.now().plusMinutes(16))
                .item(item)
                .booker(userTwo)
                .status(BookingStatus.WAITING)
                .build();

        when(bookingRepository.save(any())).thenReturn(booking);
        when(bookingRepository.findById(any())).thenReturn(Optional.of(booking));
        when(userRepository.findById(any())).thenReturn(Optional.of(userTwo));
        when(bookingService.findAllByOwner("FUTURE", userOne.getId(), Pageable.unpaged())).thenReturn(Collections.emptyList());
        BookingDto bookingDto = bookingService.update(userOne.getId(), 1L, true);

        assertEquals(bookingRequest.getStart().format(formatter), bookingDto.getStart().format(formatter));
        assertEquals(bookingRequest.getEnd().format(formatter), bookingDto.getEnd().format(formatter));
        assertEquals(bookingRequest.getItemId(), bookingDto.getItem().getId());
        assertEquals(BookingStatus.APPROVED, bookingDto.getStatus());
    }

    @Test
    void findAllByOwnerAndStateWaiting() {
        User userOne = new User(1L, "testNameOne", "testEmailOne@gmail.com");
        User userTwo = new User(2L, "testNameTwo", "testEmailTwo@gmail.com");
        Item item = new Item(1L, "itemName", "itemDescription", true, userOne, null);
        BookingRequest bookingRequest = BookingRequest.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now().plusMinutes(8))
                .end(LocalDateTime.now().plusMinutes(16))
                .build();
        Booking booking = Booking.builder()
                .id(item.getId())
                .start(LocalDateTime.now().plusMinutes(8))
                .end(LocalDateTime.now().plusMinutes(16))
                .item(item)
                .booker(userTwo)
                .status(BookingStatus.WAITING)
                .build();

        when(bookingRepository.save(any())).thenReturn(booking);
        when(bookingRepository.findById(any())).thenReturn(Optional.of(booking));
        when(userRepository.findById(any())).thenReturn(Optional.of(userTwo));
        when(bookingService.findAllByOwner("WAITING", userOne.getId(), Pageable.unpaged())).thenReturn(Collections.emptyList());
        BookingDto bookingDto = bookingService.update(userOne.getId(), 1L, true);

        assertEquals(bookingRequest.getStart().format(formatter), bookingDto.getStart().format(formatter));
        assertEquals(bookingRequest.getEnd().format(formatter), bookingDto.getEnd().format(formatter));
        assertEquals(bookingRequest.getItemId(), bookingDto.getItem().getId());
        assertEquals(BookingStatus.APPROVED, bookingDto.getStatus());
    }

    @Test
    void findAllByOwnerAndStateRejected() {
        User userOne = new User(1L, "testNameOne", "testEmailOne@gmail.com");
        User userTwo = new User(2L, "testNameTwo", "testEmailTwo@gmail.com");
        Item item = new Item(1L, "itemName", "itemDescription", true, userOne, null);
        BookingRequest bookingRequest = BookingRequest.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now().plusMinutes(8))
                .end(LocalDateTime.now().plusMinutes(16))
                .build();
        Booking booking = Booking.builder()
                .id(item.getId())
                .start(LocalDateTime.now().plusMinutes(8))
                .end(LocalDateTime.now().plusMinutes(16))
                .item(item)
                .booker(userTwo)
                .status(BookingStatus.WAITING)
                .build();

        when(bookingRepository.save(any())).thenReturn(booking);
        when(bookingRepository.findById(any())).thenReturn(Optional.of(booking));
        when(userRepository.findById(any())).thenReturn(Optional.of(userTwo));
        when(bookingService.findAllByOwner("REJECTED", userOne.getId(), Pageable.unpaged())).thenReturn(Collections.emptyList());
        BookingDto bookingDto = bookingService.update(userOne.getId(), 1L, true);

        assertEquals(bookingRequest.getStart().format(formatter), bookingDto.getStart().format(formatter));
        assertEquals(bookingRequest.getEnd().format(formatter), bookingDto.getEnd().format(formatter));
        assertEquals(bookingRequest.getItemId(), bookingDto.getItem().getId());
        assertEquals(BookingStatus.APPROVED, bookingDto.getStatus());
    }
}