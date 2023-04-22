package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.exception.model.NotUniqueEmailException;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoForUpdate;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public UserDto save(UserDto userDto) {
        log.info("Сохранение пользователя.");
        User user = UserMapper.toUser(userDto);
        emailCheck(user.getEmail());
        return UserMapper.toUserDto(userRepository.save(user));
    }

    @Override
    public UserDto update(Long userId, UserDtoForUpdate userDtoForUpdate) {
        log.info(String.format("Обновление пользователя c ID:%d", userId));
        User userForUpdate = userRepository.findById(userId).orElseThrow(()
                -> new NotFoundException(String.format("Пользователя с ID:%d нет в базе", userId)));

        if (userDtoForUpdate.getEmail() != null && !userDtoForUpdate.getEmail().equals(userForUpdate.getEmail())) {
            emailCheck(userDtoForUpdate.getEmail());
            userForUpdate.setEmail(userDtoForUpdate.getEmail());
        }

        if (userDtoForUpdate.getName() != null) {
            userForUpdate.setName(userDtoForUpdate.getName());
        }

        return UserMapper.toUserDto(userRepository.update(userForUpdate));
    }

    @Override
    public void delete(Long userId) {
        log.info(String.format("Удаление пользователя c ID:%d", userId));
        userRepository.delete(userId);
    }

    @Override
    public UserDto findById(Long userId) {
        log.info(String.format("Поиск пользователя c ID:%d", userId));
        User user = userRepository.findById(userId).orElseThrow(()
                -> new NotFoundException(String.format("Пользователя с ID:%d нет в базе", userId)));
        return UserMapper.toUserDto(user);
    }

    @Override
    public List<UserDto> findAll() {
        log.info("Поиск всех пользователей.");
        return userRepository.findAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    private void emailCheck(String email) {
        userRepository.findByEmail(email).ifPresent(user -> {
            throw new NotUniqueEmailException(String.format("Пользователь с email:%s уже зарегистрирован", email));
        });
    }
}
