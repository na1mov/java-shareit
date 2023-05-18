package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDtoShort;
import ru.practicum.shareit.booking.mapper.BookingMapperShort;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.model.MyValidationException;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.exception.model.WrongUserIdException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDtoEnhanced;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoForUpdate;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static ru.practicum.shareit.booking.model.BookingStatus.APPROVED;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserService userService;
    private final ItemMapper itemMapper;
    private final UserMapper userMapper;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    @Transactional
    public ItemDto save(Long userId, ItemDto itemDto) {
        log.info("Сохранение новой вещи");
        Item item = itemMapper.itemDtoToItem(itemDto);
        item.setOwner(userMapper.userDtoToUser(userService.findById(userId)));
        return itemMapper.itemToItemDto(itemRepository.save(item));
    }

    @Override
    @Transactional
    public ItemDto update(Long itemId, Long userId, ItemDtoForUpdate itemDtoForUpdate) {
        log.info(String.format("Обновление вещи c ID:%d", itemId));
        Item itemForUpdate = findItemById(itemId);

        if (!Objects.equals(userId, itemForUpdate.getOwner().getId())) {
            throw new WrongUserIdException(
                    String.format("Пользователь с ID:%d не является владельцем вещи с ID:%d", userId, itemId));
        }

        if (itemDtoForUpdate.getName() != null) {
            itemForUpdate.setName(itemDtoForUpdate.getName());
        }

        if (itemDtoForUpdate.getDescription() != null) {
            itemForUpdate.setDescription(itemDtoForUpdate.getDescription());
        }

        if (itemDtoForUpdate.getAvailable() != null) {
            itemForUpdate.setAvailable(itemDtoForUpdate.getAvailable());
        }

        return itemMapper.itemToItemDto(itemRepository.save(itemForUpdate));
    }

    @Override
    @Transactional
    public void delete(Long itemId) {
        log.info(String.format("Удаление вещи с ID:%d", itemId));
        findItemById(itemId);   // проверка на наличие вещи с таким ID в базе
        itemRepository.deleteById(itemId);
    }

    @Override
    public ItemDtoEnhanced findById(Long itemId, Long userId) {
        log.info(String.format("Поиск вещи с ID:%d", itemId));
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new NotFoundException(String.format("Вещи с ID:%d нет в базе", itemId)));

        ItemDtoEnhanced itemDtoEnhanced = itemMapper.itemToItemDtoEnhanced(item);

        Booking last = null;
        Booking next = null;

        if (Objects.equals(item.getOwner().getId(), userId)) {
            LocalDateTime now = LocalDateTime.now();
            last = bookingRepository.findFirstByItemAndStartLessThanEqualAndStatusIsOrderByStartDesc(
                    item, now, APPROVED).orElse(null);
            next = bookingRepository.findFirstByItemAndStartAfterAndStatusIsOrderByStartAsc(
                    item, now, APPROVED).orElse(null);
        }

        List<Comment> comments = commentRepository.findAllByItemId(item.getId());
        List<CommentDto> commentsDto = comments.stream()
                .map(CommentMapper::commentToCommentDto).collect(Collectors.toList());

        if (last != null) {
            itemDtoEnhanced.setLastBooking(BookingMapperShort.bookingToBookingDtoShort(last));
        }
        if (next != null) {
            itemDtoEnhanced.setNextBooking(BookingMapperShort.bookingToBookingDtoShort(next));
        }
        itemDtoEnhanced.setComments(commentsDto);

        return itemDtoEnhanced;
    }

    @Override
    public List<ItemDtoEnhanced> findByUserId(Long userId, Pageable pageable) {
        log.info(String.format("Поиск всех вещей пользователя с ID:%d", userId));
        userService.findById(userId);
        List<Item> itemList = itemRepository.findAllByOwnerIdOrderByIdAsc(userId, pageable);

        List<ItemDtoEnhanced> itemDtoEnhancedList = itemList.stream()
                .map(itemMapper::itemToItemDtoEnhanced)
                .collect(Collectors.toList());

        List<Comment> commentList = commentRepository.findAllByItemIn(itemList);
        Map<Long, List<Comment>> itemIdToListComments = commentList.stream()
                .collect(Collectors.groupingBy(comment -> comment.getItem().getId(), Collectors.toList()));

        LocalDateTime now = LocalDateTime.now();

        List<Booking> last = bookingRepository
                .findAllByItemInAndStartLessThanEqualAndStatusIsOrderByStartDesc(itemList, now, APPROVED);
        List<Booking> next = bookingRepository
                .findAllByItemInAndStartAfterAndStatusIsOrderByStartAsc(itemList, now, APPROVED);

        Map<Long, List<Booking>> itemIdToListLast = last.stream()
                .collect(Collectors.groupingBy(booking -> booking.getItem().getId(), Collectors.toList()));
        Map<Long, List<Booking>> itemIdToListNext = next.stream()
                .collect(Collectors.groupingBy(booking -> booking.getItem().getId(), Collectors.toList()));

        itemDtoEnhancedList.forEach(i -> i.setLastBooking(getBookingDtoShort(i.getId(), itemIdToListLast)));
        itemDtoEnhancedList.forEach(i -> i.setNextBooking(getBookingDtoShort(i.getId(), itemIdToListNext)));
        itemDtoEnhancedList.forEach(i -> i.setComments(getCommentDto(i.getId(), itemIdToListComments)));

        return itemDtoEnhancedList;
    }

    @Override
    public List<ItemDto> findByWord(String word, Pageable pageable) {
        log.info(String.format("Поиск вещи по запросу:%s", word));
        if (word.isBlank()) {
            return new ArrayList<>();
        }
        return itemRepository.findByWord(word, pageable).stream()
                .map(itemMapper::itemToItemDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CommentDto saveComment(CommentDto commentDto, long userId, long itemId) {
        User user = userMapper.userDtoToUser(userService.findById(userId));
        Item item = findItemById(itemId);
        if (bookingRepository.findAllByBookerIdAndItemIdAndStatusAndEndBefore(
                userId, itemId, APPROVED, LocalDateTime.now()).isEmpty()) {
            throw new MyValidationException(String.format("Пользователь с ID:%d не брал в аренду эту вещь.", userId));
        }
        Comment comment = CommentMapper.commentDtoToComment(commentDto);
        comment.setItem(item);
        comment.setAuthor(user);
        return CommentMapper.commentToCommentDto(commentRepository.save(comment));
    }

    private Item findItemById(Long itemId) {
        return itemRepository.findById(itemId).orElseThrow(() ->
                new NotFoundException(String.format("Вещи с ID:%d нет в базе", itemId)));
    }

    private BookingDtoShort getBookingDtoShort(Long itemDtoEnhancedId, Map<Long, List<Booking>> itemIdToListBooking) {
        Optional<BookingDtoShort> bookingDtoShort = Optional.empty();
        if (itemIdToListBooking.containsKey(itemDtoEnhancedId)) {
            bookingDtoShort = itemIdToListBooking.get(itemDtoEnhancedId).stream()
                    .map(BookingMapperShort::bookingToBookingDtoShort).findFirst();
        }
        return bookingDtoShort.orElse(null);
    }

    private List<CommentDto> getCommentDto(Long itemDtoEnhancedId, Map<Long, List<Comment>> itemIdToListComments) {
        if (itemIdToListComments.containsKey(itemDtoEnhancedId)) {
            return itemIdToListComments.getOrDefault(itemDtoEnhancedId, null).stream()
                    .map(CommentMapper::commentToCommentDto).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }
}
