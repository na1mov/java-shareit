package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoForUpdate;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto save(@RequestHeader(name = "X-Sharer-User-Id") long userId, @Valid @RequestBody ItemDto itemDto) {
        return itemService.save(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@PathVariable long itemId,
                          @RequestHeader(name = "X-Sharer-User-Id") long userId,
                          @Valid @RequestBody ItemDtoForUpdate itemDtoForUpdate) {
        return itemService.update(itemId, userId, itemDtoForUpdate);
    }

    @DeleteMapping("/{itemId}")
    public void delete(@PathVariable long itemId) {
        itemService.delete(itemId);
    }

    @GetMapping("/{itemId}")
    public ItemDto findByItemId(@PathVariable long itemId) {
        return itemService.findById(itemId);
    }

    @GetMapping
    public List<ItemDto> findByUserId(@RequestHeader(name = "X-Sharer-User-Id") long userId) {
        return itemService.findByUserId(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> findByWord(@RequestParam(required = false) String text) {
        return itemService.findByWord(text);
    }
}
