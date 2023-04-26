package ru.practicum.shareit.item.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class InMemoryItemRepository implements ItemRepository {
    private final Map<Long, Item> itemMap = new HashMap<>();
    long count = 1;


    @Override
    public Item save(Item item) {
        item.setId(count++);
        itemMap.put(item.getId(), item);
        return item;
    }

    @Override
    public Item update(Item item) {
        itemMap.put(item.getId(), item);
        return item;
    }

    @Override
    public void delete(Long id) {
        itemMap.remove(id);
    }

    @Override
    public Optional<Item> findById(Long id) {
        return Optional.ofNullable(itemMap.get(id));
    }

    @Override
    public List<Item> findByUserId(Long userId) {
        return itemMap.values().stream()
                .filter(item -> Objects.equals(item.getOwner().getId(), userId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> findByWord(String word) {
        if (word.isBlank()) {
            return new ArrayList<>();
        }

        return itemMap.values().stream()
                .filter(item -> (item.getName().toLowerCase().contains(word.toLowerCase())
                                || item.getDescription().toLowerCase().contains(word.toLowerCase()))
                                && item.getAvailable())
                .collect(Collectors.toList());
    }
}
