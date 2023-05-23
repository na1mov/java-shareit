package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoForUpdate;

import java.util.List;

public interface UserService {
    UserDto save(UserDto userDto);

    UserDto update(Long id, UserDtoForUpdate userDtoForUpdate);

    void delete(Long id);

    UserDto findById(Long id);

    List<UserDto> findAll();
}
