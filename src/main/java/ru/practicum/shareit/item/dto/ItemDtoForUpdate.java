package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ItemDtoForUpdate {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
}
