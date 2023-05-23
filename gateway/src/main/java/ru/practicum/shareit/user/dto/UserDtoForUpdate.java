package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Value;

import javax.validation.constraints.Email;

@Value
@Builder
public class UserDtoForUpdate {
    Long id;
    String name;
    @Email
    String email;
}
