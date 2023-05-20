package ru.practicum.shareit.request.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto save(ItemRequestDto itemRequestDto, Long ownerId);

    ItemRequestDto findById(Long ownerId, Long requestId);

    List<ItemRequestDto> findByOwnerId(Long ownerId);

    List<ItemRequestDto> findAll(Long ownerId, Pageable pageable);
}
