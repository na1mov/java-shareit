package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ItemDtoForUpdate {
    Long id;
    String name;
    String description;
    Boolean available;
}
