package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoEnhanced;
import ru.practicum.shareit.item.dto.ItemDtoForUpdate;

import java.util.List;

public interface ItemService {
    ItemDto save(Long userId, ItemDto itemDto);

    ItemDto update(Long itemId, Long userId, ItemDtoForUpdate itemDtoForUpdate);

    void delete(Long itemId);

    ItemDtoEnhanced findById(Long itemId, Long userId);

    List<ItemDtoEnhanced> findByUserId(Long userId);

    List<ItemDto> findByWord(String word);

    CommentDto saveComment(CommentDto comment, long userId, long itemId);
}
