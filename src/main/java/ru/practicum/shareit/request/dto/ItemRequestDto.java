package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Value;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Value
@Builder
public class ItemRequestDto {
    Long id;
    String description;
    User requester;
    LocalDateTime created;
}
