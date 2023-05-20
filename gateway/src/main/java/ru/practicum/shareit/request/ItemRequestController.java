package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@Slf4j
@Validated
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody ItemRequestDto itemRequestDto,
                                         @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Create request by userId={}", userId);
        return itemRequestClient.save(itemRequestDto, userId);
    }

    @GetMapping
    public ResponseEntity<Object> findByOwnerId(@RequestHeader("X-Sharer-User-Id") long ownerId) {
        log.info("Find request by ownerId={}", ownerId);
        return itemRequestClient.findByOwnerId(ownerId);

    }

    @GetMapping("/all")
    public ResponseEntity<Object> findAll(@RequestHeader("X-Sharer-User-Id") long userId,
                                          @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                          @Positive @RequestParam(defaultValue = "10") Integer size) {
        log.info("Find all requests by userId={}", userId);
        return itemRequestClient.findAll(from, size, userId);
    }

    @GetMapping("{requestId}")
    public ResponseEntity<Object> findById(@RequestHeader("X-Sharer-User-Id") long userId,
                                           @PathVariable long requestId) {
        log.info("Find request by requestId={}", requestId);
        return itemRequestClient.findById(userId, requestId);
    }
}
