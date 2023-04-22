package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Value;

import javax.validation.constraints.Email;

@Value
@Builder
public class UserDtoForUpdate {
    private Long id;
    private String name;
    @Email
    private String email;
}
