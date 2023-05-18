package ru.practicum.shareit.request.dto;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
class ItemRequestDtoTest {
    @Autowired
    JacksonTester<ItemRequestDto> json;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    @SneakyThrows
    @Test
    void itemRequestDtoTest() {
        User user = User.builder()
                .id(1L)
                .name("testName")
                .email("testEmail@gmail.com")
                .build();
        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .id(1L)
                .description("test description")
                .requester(user)
                .created(LocalDateTime.of(2033, 1, 2, 3, 4))
                .build();
        JsonContent<ItemRequestDto> jsonTest = json.write(itemRequestDto);
        assertThat(jsonTest).extractingJsonPathNumberValue("$.id")
                .isEqualTo(itemRequestDto.getId().intValue());
        assertThat(jsonTest).extractingJsonPathStringValue("$.description")
                .isEqualTo(itemRequestDto.getDescription());
        assertThat(jsonTest).extractingJsonPathNumberValue("$.requester.id")
                .isEqualTo(itemRequestDto.getRequester().getId().intValue());
        assertThat(jsonTest).extractingJsonPathStringValue("$.requester.name")
                .isEqualTo(itemRequestDto.getRequester().getName());
        assertThat(jsonTest).extractingJsonPathStringValue("$.requester.email")
                .isEqualTo(itemRequestDto.getRequester().getEmail());
        assertThat(jsonTest).extractingJsonPathStringValue("$.created")
                .isEqualTo(itemRequestDto.getCreated().format(formatter));
    }
}