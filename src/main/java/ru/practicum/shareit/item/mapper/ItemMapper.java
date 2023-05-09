package ru.practicum.shareit.item.mapper;

import org.mapstruct.Mapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoEnhanced;
import ru.practicum.shareit.item.model.Item;

@Mapper(componentModel = "spring")
public interface ItemMapper {
    Item itemDtoToItem(ItemDto itemDto);

    ItemDto itemToItemDto(Item item);

    ItemDtoEnhanced itemToItemDtoEnhanced(Item item);
}

