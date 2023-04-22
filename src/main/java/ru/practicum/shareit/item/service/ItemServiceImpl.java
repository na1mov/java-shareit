package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.exception.model.WrongUserIdException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoForUpdate;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserService userService;

    @Override
    public ItemDto save(Long userId, ItemDto itemDto) {
        log.info("Сохранение новой вещи");
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(UserMapper.toUser(userService.findById(userId)));
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public ItemDto update(Long itemId, Long userId, ItemDtoForUpdate itemDtoForUpdate) {
        log.info(String.format("Обновление вещи c ID:%d", itemId));
        Item itemForUpdate = findItemById(itemId);

        if (!Objects.equals(userId, itemForUpdate.getOwner().getId())) {
            throw new WrongUserIdException(
                    String.format("Пользователь с ID:%d не является владельцем вещи с ID:%d", userId, itemId));
        }

        if (itemDtoForUpdate.getName() != null) {
            itemForUpdate.setName(itemDtoForUpdate.getName());
        }

        if (itemDtoForUpdate.getDescription() != null) {
            itemForUpdate.setDescription(itemDtoForUpdate.getDescription());
        }

        if (itemDtoForUpdate.getAvailable() != null) {
            itemForUpdate.setAvailable(itemDtoForUpdate.getAvailable());
        }

        return ItemMapper.toItemDto(itemRepository.update(itemForUpdate));
    }

    @Override
    public void delete(Long itemId) {
        log.info(String.format("Удаление вещи с ID:%d", itemId));
        itemRepository.delete(itemId);
    }

    @Override
    public ItemDto findById(Long itemId) {
        log.info(String.format("Поиск вещи с ID:%d", itemId));
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new NotFoundException(String.format("Вещи с ID:%d нет в базе", itemId)));
        return ItemMapper.toItemDto(item);
    }

    @Override
    public List<ItemDto> findByUserId(Long userId) {
        log.info(String.format("Поиск всех вещей пользователя с ID:%d", userId));
        return itemRepository.findByUserId(userId).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> findByWord(String word) {
        log.info(String.format("Поиск вещи по запросу:%s", word));
        return itemRepository.findByWord(word).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    private Item findItemById(Long itemId) {
        return itemRepository.findById(itemId).orElseThrow(() ->
                new NotFoundException(String.format("Вещи с ID:%d нет в базе", itemId)));
    }
}
