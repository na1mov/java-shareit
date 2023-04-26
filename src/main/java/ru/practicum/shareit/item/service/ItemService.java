package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoForUpdate;

import java.util.List;

public interface ItemService {
    ItemDto save(Long userId, ItemDto itemDto);

    ItemDto update(Long itemId, Long userId, ItemDtoForUpdate itemDtoForUpdate);

    void delete(Long itemId);

    ItemDto findById(Long itemId);

    List<ItemDto> findByUserId(Long userId);

    List<ItemDto> findByWord(String word);
}
