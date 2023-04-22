package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    User save(User user);

    User update(User user);

    void delete(Long id);

    List<User> findAll();

    Optional<User> findById(Long id);

    Optional<User> findByEmail(String email);
}
