package ru.practicum.shareit.user.dto;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
class UserDtoTest {
    @Autowired
    JacksonTester<UserDto> json;

    @SneakyThrows
    @Test
    void userDtoTest() {
        UserDto userDto = UserDto.builder()
                .id(1L)
                .name("testName")
                .email("testEmail@gmail.com")
                .build();
        JsonContent<UserDto> jsonTest = json.write(userDto);

        assertThat(jsonTest).extractingJsonPathNumberValue("$.id").isEqualTo(userDto.getId().intValue());
        assertThat(jsonTest).extractingJsonPathStringValue("$.name").isEqualTo(userDto.getName());
        assertThat(jsonTest).extractingJsonPathStringValue("$.email").isEqualTo(userDto.getEmail());
    }
}