package ru.practicum.shareit.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc
class UserControllerTest {
    @MockBean
    UserService userService;
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper mapper;
    private final UserDto userDto = UserDto.builder()
            .id(1L)
            .name("testName")
            .email("testEmail@gmail.com")
            .build();

    @SneakyThrows
    @Test
    void save() {
        Mockito.when(userService.save(Mockito.any())).thenReturn(userDto);
        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(userDto.getName()), String.class))
                .andExpect(jsonPath("$.email", is(userDto.getEmail()), String.class))
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class));
    }

    @SneakyThrows
    @Test
    void update() {
        UserDto userDtoUpd = UserDto.builder()
                .id(1L)
                .name("newTestName")
                .email("newTestEmail@gmail.com")
                .build();
        Mockito.when(userService.update(Mockito.anyLong(), Mockito.any())).thenReturn(userDtoUpd);
        mockMvc.perform(MockMvcRequestBuilders.patch("/users/1")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(userDtoUpd))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(userDtoUpd.getName()), String.class))
                .andExpect(jsonPath("$.email", is(userDtoUpd.getEmail()), String.class))
                .andExpect(jsonPath("$.id", is(userDtoUpd.getId()), Long.class));
    }

    @SneakyThrows
    @Test
    void delete() {
        Mockito.doNothing().when(userService).delete(Mockito.anyLong());
        mockMvc.perform(MockMvcRequestBuilders.delete("/users/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk());
    }

    @SneakyThrows
    @Test
    void findById() {
        Mockito.when(userService.findById(Mockito.anyLong())).thenReturn(userDto);
        mockMvc.perform(MockMvcRequestBuilders.get("/users/1")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(userDto.getName()), String.class))
                .andExpect(jsonPath("$.email", is(userDto.getEmail()), String.class))
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class));
    }

    @SneakyThrows
    @Test
    void findAll() {
        UserDto userDtoTwo = UserDto.builder()
                .id(2L)
                .name("testNameTwo")
                .email("testEmailTwo@gmail.com")
                .build();
        UserDto userDtoThree = UserDto.builder()
                .id(3L)
                .name("testNameThree")
                .email("testEmailThree@gmail.com")
                .build();
        List<UserDto> userDtoList = List.of(userDto, userDtoTwo, userDtoThree);
        Mockito.when(userService.findAll()).thenReturn(userDtoList);
        mockMvc.perform(MockMvcRequestBuilders.get("/users")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)));
    }
}