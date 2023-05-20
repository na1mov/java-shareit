package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoForUpdate;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.ArrayList;

@Controller
@Slf4j
@Validated
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> save(@RequestBody ItemDto itemDto,
                                       @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Create item by userId={}", userId);
        return itemClient.save(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(@PathVariable("itemId") long itemId,
                                         @Validated @RequestBody ItemDtoForUpdate itemDtoForUpdate,
                                         @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Update item by itemId={}", itemId);
        return itemClient.update(itemId, itemDtoForUpdate, userId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> findByItemId(@PathVariable("itemId") long itemId,
                                               @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Find item by itemId={}", itemId);
        return itemClient.findByItemId(itemId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> findByUserId(@RequestHeader("X-Sharer-User-Id") long userId,
                                               @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                               @Positive @RequestParam(defaultValue = "10") Integer size) {
        log.info("Find items by userId={}", userId);
        return itemClient.findByUserId(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> findByWord(@RequestParam String text,
                                             @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                             @Positive @RequestParam(defaultValue = "10") Integer size) {
        log.info("Find items by word '{}'", text);
        return text.isBlank() ? ResponseEntity.ok(new ArrayList<>()) : itemClient.findByWord(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> saveComment(@Valid @RequestBody CommentDto commentDto,
                                              @RequestHeader("X-Sharer-User-Id") long userId,
                                              @PathVariable long itemId) {
        log.info("Create comment by userId={}", userId);
        return itemClient.saveComment(commentDto, userId, itemId);
    }
}
