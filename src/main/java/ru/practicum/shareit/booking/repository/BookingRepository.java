package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByBookerIdOrderByStartDesc(Long bookerId);

    List<Booking> findAllByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(
            Long bookerId, LocalDateTime start, LocalDateTime end);

    List<Booking> findAllByBookerIdAndEndIsBeforeOrderByStartDesc(Long bookerId, LocalDateTime start);

    List<Booking> findAllByBookerIdAndStartIsAfterOrderByStartDesc(Long bookerId, LocalDateTime start);

    List<Booking> findAllByBookerIdAndStatusOrderByStartDesc(Long bookerId, BookingStatus status);

    List<Booking> findAllByItemOwnerIdOrderByStartDesc(Long bookerId);

    List<Booking> findAllByItemOwnerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(
            Long bookerId, LocalDateTime start, LocalDateTime end);

    List<Booking> findAllByItemOwnerIdAndEndIsBeforeOrderByStartDesc(Long bookerId, LocalDateTime start);

    List<Booking> findAllByItemOwnerIdAndStartIsAfterOrderByStartDesc(Long bookerId, LocalDateTime start);

    List<Booking> findAllByItemOwnerIdAndStatusOrderByStartDesc(Long bookerId, BookingStatus status);

    List<Booking> findAllByBookerIdAndItemIdAndStatusAndEndBefore(
            Long userId, Long itemId, BookingStatus status, LocalDateTime end);

    Optional<Booking> findFirstByItemAndStartLessThanEqualAndStatusIsOrderByStartDesc(
            Item itemId, LocalDateTime now, BookingStatus status);

    Optional<Booking> findFirstByItemAndStartAfterAndStatusIsOrderByStartAsc(
            Item itemId, LocalDateTime now, BookingStatus status);

    List<Booking> findAllByItemInAndStartLessThanEqualAndStatusIsOrderByStartDesc(
            List<Item> items, LocalDateTime now, BookingStatus status);

    List<Booking> findAllByItemInAndStartAfterAndStatusIsOrderByStartAsc(
            List<Item> items, LocalDateTime now, BookingStatus status);
}
