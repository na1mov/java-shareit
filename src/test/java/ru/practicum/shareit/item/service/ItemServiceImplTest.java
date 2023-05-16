package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.Mockito;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.model.WrongUserIdException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoEnhanced;
import ru.practicum.shareit.item.dto.ItemDtoForUpdate;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.mapper.ItemMapperImpl;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

class ItemServiceImplTest {
    @Mock
    ItemRequestRepository itemRequestRepository;
    @Mock
    ItemRepository itemRepository;
    @Mock
    UserService userService;
    @Mock
    ItemMapper itemMapper;
    @Mock
    UserMapper userMapper;
    @Mock
    BookingRepository bookingRepository;
    @Mock
    CommentRepository commentRepository;
    ItemService itemService;

    @BeforeEach
    void beforeEach() {
        itemRequestRepository = Mockito.mock(ItemRequestRepository.class);
        itemRepository = Mockito.mock(ItemRepository.class);
        userService = Mockito.mock(UserService.class);
        itemMapper = new ItemMapperImpl(itemRequestRepository);
        userMapper = Mappers.getMapper(UserMapper.class);
        bookingRepository = Mockito.mock(BookingRepository.class);
        commentRepository = Mockito.mock(CommentRepository.class);
        itemService = new ItemServiceImpl(itemRepository,
                userService,
                itemMapper,
                userMapper,
                bookingRepository,
                commentRepository);
    }

    @Test
    void save() {
        User user = new User(1L, "testName", "testEmail@gmail.com");
        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .name("itemName")
                .description("itemDescription")
                .build();

        when(itemRepository.save(any())).thenReturn(itemMapper.itemDtoToItem(itemDto));
        when(userService.findById(any())).thenReturn(userMapper.userToUserDto(user));
        ItemDto itemDtoTest = itemService.save(user.getId(), itemDto);

        assertEquals(itemDto.getName(), itemDtoTest.getName());
        assertEquals(itemDto.getAvailable(), itemDtoTest.getAvailable());
        assertEquals(itemDto.getDescription(), itemDtoTest.getDescription());
    }

    @Test
    void update() {
        User user = new User(1L, "testName", "testEmail@gmail.com");
        Item item = Item.builder()
                .id(1L)
                .name("itemName")
                .description("itemDescription")
                .available(true)
                .owner(user)
                .build();
        ItemDtoForUpdate itemDtoForUpdate = ItemDtoForUpdate.builder()
                .id(1L)
                .name("itemNameUpd")
                .description("itemDescriptionUpd")
                .available(true)
                .build();
        ItemDto itemDtoUpd = ItemDto.builder()
                .id(1L)
                .name("itemNameUpd")
                .description("itemDescriptionUpd")
                .available(true)
                .build();

        when(itemRepository.findById(any())).thenReturn(Optional.of(item));
        when(itemRepository.save(any())).thenReturn(itemMapper.itemDtoToItem(itemDtoUpd));
        ItemDto itemDtoTest = itemService.update(item.getId(), user.getId(), itemDtoForUpdate);

        assertEquals(itemDtoUpd.getName(), itemDtoTest.getName());
        assertEquals(itemDtoUpd.getId(), itemDtoTest.getId());
        assertEquals(itemDtoUpd.getAvailable(), itemDtoTest.getAvailable());
        assertEquals(itemDtoUpd.getDescription(), itemDtoTest.getDescription());
    }

    @Test
    void updateWithOnlyNameChange() {
        User user = new User(1L, "testName", "testEmail@gmail.com");
        Item item = Item.builder()
                .id(1L)
                .name("itemName")
                .description("itemDescription")
                .available(true)
                .owner(user)
                .build();
        ItemDtoForUpdate itemDtoForUpdate = ItemDtoForUpdate.builder()
                .id(1L)
                .name("itemNameUpd")
                .build();
        ItemDto itemDtoUpd = ItemDto.builder()
                .id(1L)
                .name("itemNameUpd")
                .description("itemDescription")
                .available(true)
                .build();

        when(itemRepository.findById(any())).thenReturn(Optional.of(item));
        when(itemRepository.save(any())).thenReturn(itemMapper.itemDtoToItem(itemDtoUpd));
        ItemDto itemDtoTest = itemService.update(item.getId(), user.getId(), itemDtoForUpdate);

        assertEquals(itemDtoUpd.getName(), itemDtoTest.getName());
        assertEquals(itemDtoUpd.getId(), itemDtoTest.getId());
        assertEquals(itemDtoUpd.getAvailable(), itemDtoTest.getAvailable());
        assertEquals(itemDtoUpd.getDescription(), itemDtoTest.getDescription());
    }

    @Test
    void updateWithWrongOwner() {
        User user = new User(1L, "testName", "testEmail@gmail.com");
        User userTwo = new User(2L, "testNameTwo", "testEmailTwo@gmail.com");
        Item item = Item.builder()
                .id(1L)
                .name("itemName")
                .description("itemDescription")
                .available(true)
                .owner(userTwo)
                .build();
        ItemDtoForUpdate itemDtoForUpdate = ItemDtoForUpdate.builder()
                .id(1L)
                .name("itemNameUpd")
                .build();
        ItemDto itemDtoUpd = ItemDto.builder()
                .id(1L)
                .name("itemNameUpd")
                .description("itemDescription")
                .available(true)
                .build();

        when(itemRepository.findById(any())).thenReturn(Optional.of(item));
        when(itemRepository.save(any())).thenReturn(itemMapper.itemDtoToItem(itemDtoUpd));

        assertThrows(WrongUserIdException.class, () -> itemService.update(item.getId(), user.getId(), itemDtoForUpdate));
    }

    @Test
    void delete() {
        User user = new User(1L, "testName", "testEmail@gmail.com");
        Item item = Item.builder()
                .id(1L)
                .name("itemName")
                .description("itemDescription")
                .available(true)
                .owner(user)
                .build();

        when(itemRepository.findById(any())).thenReturn(Optional.of(item));
        itemService.delete(1L);
        Mockito.verify(itemRepository, Mockito.times(1)).deleteById(1L);
    }

    @Test
    void findById() {
        User user = new User(1L, "testName", "testEmail@gmail.com");
        Item item = Item.builder()
                .id(1L)
                .name("itemName")
                .description("itemDescription")
                .available(true)
                .owner(user)
                .build();

        when(itemRepository.findById(any())).thenReturn(Optional.of(item));
        when(commentRepository.findAllByItemId(anyLong())).thenReturn(new ArrayList<>());
        ItemDtoEnhanced itemDtoEnhanced = itemService.findById(item.getId(), user.getId());

        assertEquals(item.getId(), itemDtoEnhanced.getId());
        assertEquals(item.getName(), itemDtoEnhanced.getName());
        assertEquals(item.getAvailable(), itemDtoEnhanced.getAvailable());
        assertEquals(item.getDescription(), itemDtoEnhanced.getDescription());
    }

    @Test
    void findByUserId() {
        User user = new User(1L, "testName", "testEmail@gmail.com");
        Item item = Item.builder()
                .id(1L)
                .name("itemName")
                .description("itemDescription")
                .available(true)
                .owner(user)
                .build();

        when(itemRepository.findAllByOwnerIdOrderByIdAsc(any())).thenReturn(List.of(item));
        when(userService.findById(any())).thenReturn(userMapper.userToUserDto(user));
        when(itemRepository.findById(any())).thenReturn(Optional.of(item));
        List<ItemDtoEnhanced> ideList = itemService.findByUserId(user.getId());

        assertEquals(1, ideList.size());
        assertEquals(item.getId(), ideList.get(0).getId());
        assertEquals(item.getName(), ideList.get(0).getName());
        assertEquals(item.getAvailable(), ideList.get(0).getAvailable());
        assertEquals(item.getDescription(), ideList.get(0).getDescription());
    }

    @Test
    void findByWord() {
        User user = new User(1L, "testName", "testEmail@gmail.com");
        Item item = Item.builder()
                .id(1L)
                .name("itemName")
                .description("itemDescription")
                .available(true)
                .owner(user)
                .build();

        when(itemRepository.findByWord(anyString())).thenReturn(List.of(item));
        List<ItemDto> itemList = itemService.findByWord("TEMNA");

        assertEquals(1, itemList.size());
        assertEquals(item.getId(), itemList.get(0).getId());
        assertEquals(item.getName(), itemList.get(0).getName());
        assertEquals(item.getAvailable(), itemList.get(0).getAvailable());
        assertEquals(item.getDescription(), itemList.get(0).getDescription());
    }

    @Test
    void saveComment() {
        User userOne = new User(1L, "testNameOne", "testEmailOne@gmail.com");
        Item item = Item.builder()
                .id(1L)
                .name("itemName")
                .description("itemDescription")
                .available(true)
                .owner(userOne)
                .build();
        User userTwo = new User(2L, "testNameTwo", "testEmailTwo@gmail.com");
        Booking booking = new Booking(1L, LocalDateTime.now().plusMinutes(8),
                LocalDateTime.now().minusMinutes(16), item, userTwo, BookingStatus.APPROVED);
        CommentDto commentDto = CommentDto.builder()
                .id(1L)
                .text("comment info")
                .created(LocalDateTime.now())
                .build();
        Comment comment = Comment.builder()
                .id(1L)
                .text("comment info")
                .author(userTwo)
                .created(LocalDateTime.now())
                .build();

        when(userService.findById(any())).thenReturn(userMapper.userToUserDto(userTwo));
        when(itemRepository.findById(any())).thenReturn(Optional.of(item));
        when(bookingRepository.findAllByBookerIdAndItemIdAndStatusAndEndBefore(any(), any(),
                any(), any())).thenReturn(List.of(booking));
        when(commentRepository.save(any())).thenReturn(comment);
        CommentDto commentDtoTest = itemService.saveComment(commentDto, userTwo.getId(), item.getId());

        assertEquals(commentDtoTest.getText(), commentDto.getText());
    }
}