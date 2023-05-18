package ru.practicum.shareit.user.dto;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
class UserDtoForUpdateTest {

    @Autowired
    JacksonTester<UserDtoForUpdate> json;

    @SneakyThrows
    @Test
    void userDtoForUpdateTest() {
        UserDtoForUpdate userDtoForUpdate = UserDtoForUpdate.builder()
                .id(1L)
                .name("testName")
                .email("testEmail@gmail.com")
                .build();
        JsonContent<UserDtoForUpdate> jsonTest = json.write(userDtoForUpdate);
        assertThat(jsonTest).extractingJsonPathNumberValue("$.id").isEqualTo(userDtoForUpdate.getId().intValue());
        assertThat(jsonTest).extractingJsonPathStringValue("$.name").isEqualTo(userDtoForUpdate.getName());
        assertThat(jsonTest).extractingJsonPathStringValue("$.email").isEqualTo(userDtoForUpdate.getEmail());
    }
}