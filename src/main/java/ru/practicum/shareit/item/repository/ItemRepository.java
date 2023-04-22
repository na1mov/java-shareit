package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {
    Item save(Item item);

    Item update(Item item);

    void delete(Long id);

    Optional<Item> findById(Long id);

    List<Item> findByUserId(Long userId);

    List<Item> findByWord(String word);
}
