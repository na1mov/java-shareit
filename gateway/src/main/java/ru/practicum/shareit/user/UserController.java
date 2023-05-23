package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoForUpdate;

@RestController
@Slf4j
@Validated
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {
    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> save(@RequestBody UserDto userDto) {
        log.info("Create user {}", userDto);
        return userClient.save(userDto);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> update(@PathVariable("userId") long userId,
                                         @RequestBody UserDtoForUpdate userDtoForUpdate) {
        log.info("Update user by userId={}", userId);
        return userClient.update(userId, userDtoForUpdate);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> delete(@PathVariable("userId") long userId) {
        log.info("Delete user by userId={}", userId);
        return userClient.delete(userId);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> findById(@PathVariable("userId") long userId) {
        log.info("Find user by userId={}", userId);
        return userClient.findById(userId);
    }

    @GetMapping
    public ResponseEntity<Object> findAll() {
        log.info("Find all users");
        return userClient.findAll();
    }
}
