package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto save(@Valid @RequestBody ItemRequestDto itemRequestDto,
                               @RequestHeader("X-Sharer-User-Id") long ownerId) {
        return itemRequestService.save(itemRequestDto, ownerId);
    }

    @GetMapping
    public List<ItemRequestDto> findByOwnerId(@RequestHeader("X-Sharer-User-Id") long ownerId) {
        return itemRequestService.findByOwnerId(ownerId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> findAll(@RequestHeader("X-Sharer-User-Id") long ownerId,
                                        @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                        @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return itemRequestService.findAll(ownerId, PageRequest.of((from == 0 ? 0 : (from / size)), size));
    }

    @GetMapping("{requestId}")
    public ItemRequestDto findById(@RequestHeader("X-Sharer-User-Id") long ownerId, @PathVariable long requestId) {
        return itemRequestService.findById(ownerId, requestId);
    }
}
