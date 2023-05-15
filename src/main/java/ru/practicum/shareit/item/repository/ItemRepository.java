package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findAllByOwnerIdOrderByIdAsc(Long userId);

    @Query("SELECT i FROM Item i " +
            "WHERE UPPER(i.name) LIKE UPPER(CONCAT('%', ?1, '%')) " +
            " OR UPPER(i.description) LIKE UPPER(CONCAT('%', ?1, '%')) " +
            "AND i.available=TRUE")
    List<Item> findByWord(String text);

    List<Item> findAllByRequestIn(List<ItemRequest> itemRequests);

    List<Item> findAllByRequest(ItemRequest itemRequest);
}
