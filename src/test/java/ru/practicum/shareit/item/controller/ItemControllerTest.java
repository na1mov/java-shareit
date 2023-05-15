package ru.practicum.shareit.item.controller;

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
import ru.practicum.shareit.booking.dto.BookingDtoShort;
import ru.practicum.shareit.exception.model.ErrorResponse;
import ru.practicum.shareit.exception.model.WrongUserIdException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoEnhanced;
import ru.practicum.shareit.item.dto.ItemDtoForUpdate;
import ru.practicum.shareit.item.service.ItemService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
@AutoConfigureMockMvc
class ItemControllerTest {
    @MockBean
    ItemService itemService;
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper mapper;
    private final ItemDto itemDto = ItemDto.builder()
            .id(1L)
            .name("itemName")
            .description("itemDescription")
            .available(true)
            .build();
    private final ItemDtoEnhanced itemDtoEnhanced = ItemDtoEnhanced.builder()
            .id(1L)
            .name("itemName")
            .description("itemDescription")
            .available(true)
            .lastBooking(BookingDtoShort.builder()
                    .id(1L)
                    .bookerId(11L)
                    .build())
            .nextBooking(BookingDtoShort.builder()
                    .id(2L)
                    .bookerId(22L)
                    .build())
            .comments(List.of(CommentDto.builder()
                    .id(1L)
                    .text("comment info")
                    .created(LocalDateTime.now())
                    .build()))
            .build();

    @SneakyThrows
    @Test
    void save() {
        when(itemService.save(anyLong(), any())).thenReturn(itemDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(itemDto.getName()), String.class))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription()), String.class))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable()), Boolean.class))
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class));
    }

    @SneakyThrows
    @Test
    void saveThrowsExceptionIfWrongBody() {
        ItemDto itemDtoWithNull = ItemDto.builder()
                .id(1L)
                .name("itemName")
                .available(null)
                .build();
        when(itemService.save(anyLong(), any())).thenReturn(itemDto);
        mockMvc.perform(MockMvcRequestBuilders.post("/items")
                        .header("X-Sharer-User-Id", "1")
                        .content(mapper.writeValueAsString(itemDtoWithNull))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void update() {
        ItemDtoForUpdate itemDtoForUpdate = ItemDtoForUpdate.builder()
                .id(1L)
                .name("newItemName")
                .description("newItemDescription")
                .available(true)
                .build();

        when(itemService.update(anyLong(), anyLong(), any())).thenReturn(itemDto);
        mockMvc.perform(MockMvcRequestBuilders.patch("/items/1")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(itemDtoForUpdate))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(itemDto.getName()), String.class))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription()), String.class))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable()), Boolean.class))
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class));
    }

    @SneakyThrows
    @Test
    void updateThrowsWrongUserIdException() {
        ItemDtoForUpdate itemDtoForUpdate = ItemDtoForUpdate.builder()
                .id(1L)
                .name("newItemName")
                .description("newItemDescription")
                .available(true)
                .build();

        when(itemService.update(anyLong(), anyLong(), any())).thenReturn(itemDto);

        mockMvc.perform(MockMvcRequestBuilders.patch("/items/1")
                        .header("X-Sharer-User-Id", "NotOk")
                        .content(mapper.writeValueAsString(itemDtoForUpdate))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    @SneakyThrows
    @Test
    void updateThrowsExceptionIfWrongId() {
        ItemDtoForUpdate itemDtoForUpdate = ItemDtoForUpdate.builder()
                .id(1L)
                .name("newItemName")
                .description("newItemDescription")
                .available(true)
                .build();

        ErrorResponse errorResponse = new ErrorResponse(String.format(
                "Пользователь с ID:%d не является владельцем вещи с ID:%d", 1L, itemDtoForUpdate.getId()));

        when(itemService.update(anyLong(), anyLong(), any()))
                .thenThrow(new WrongUserIdException(String.format(
                        "Пользователь с ID:%d не является владельцем вещи с ID:%d", 1L, itemDtoForUpdate.getId())));

        mockMvc.perform(MockMvcRequestBuilders.patch("/items/1")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(itemDtoForUpdate))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error", is(errorResponse.getError()), String.class));
    }

    @SneakyThrows
    @Test
    void delete() {
        Mockito.doNothing().when(itemService).delete(Mockito.anyLong());
        mockMvc.perform(MockMvcRequestBuilders.delete("/items/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk());
    }

    @SneakyThrows
    @Test
    void findByItemId() {
        when(itemService.findById(anyLong(), anyLong())).thenReturn(itemDtoEnhanced);

        mockMvc.perform(MockMvcRequestBuilders.get("/items/1")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName()), String.class))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription()), String.class))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable()), Boolean.class))
                .andExpect(jsonPath("$.lastBooking.id",
                        is(itemDtoEnhanced.getLastBooking().getId().intValue())))
                .andExpect(jsonPath("$.lastBooking.bookerId",
                        is(itemDtoEnhanced.getLastBooking().getBookerId().intValue())))
                .andExpect(jsonPath("$.nextBooking.id",
                        is(itemDtoEnhanced.getNextBooking().getId().intValue())))
                .andExpect(jsonPath("$.nextBooking.bookerId",
                        is(itemDtoEnhanced.getNextBooking().getBookerId().intValue())))
                .andExpect(jsonPath("$.comments[0].id",
                        is(itemDtoEnhanced.getComments().get(0).getId().intValue())))
                .andExpect(jsonPath("$.comments[0].text",
                        is(itemDtoEnhanced.getComments().get(0).getText())));
    }

    @SneakyThrows
    @Test
    void findByUserId() {
        when(itemService.findByUserId(anyLong())).thenReturn(List.of(itemDtoEnhanced));

        mockMvc.perform(MockMvcRequestBuilders.get("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemDto.getName()), String.class))
                .andExpect(jsonPath("$[0].description", is(itemDto.getDescription()), String.class))
                .andExpect(jsonPath("$[0].available", is(itemDto.getAvailable()), Boolean.class))
                .andExpect(jsonPath("$[0].lastBooking.id",
                        is(itemDtoEnhanced.getLastBooking().getId().intValue())))
                .andExpect(jsonPath("$[0].lastBooking.bookerId",
                        is(itemDtoEnhanced.getLastBooking().getBookerId().intValue())))
                .andExpect(jsonPath("$[0].nextBooking.id",
                        is(itemDtoEnhanced.getNextBooking().getId().intValue())))
                .andExpect(jsonPath("$[0].nextBooking.bookerId",
                        is(itemDtoEnhanced.getNextBooking().getBookerId().intValue())))
                .andExpect(jsonPath("$[0].comments[0].id",
                        is(itemDtoEnhanced.getComments().get(0).getId().intValue())))
                .andExpect(jsonPath("$[0].comments[0].text",
                        is(itemDtoEnhanced.getComments().get(0).getText())));
    }

    @SneakyThrows
    @Test
    void findByWord() {
        when(itemService.findByWord(anyString())).thenReturn(List.of(itemDto));
        mockMvc.perform(MockMvcRequestBuilders.get("/items/search")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("text", "text")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name", is(itemDto.getName()), String.class))
                .andExpect(jsonPath("$[0].description", is(itemDto.getDescription()), String.class))
                .andExpect(jsonPath("$[0].available", is(itemDto.getAvailable()), Boolean.class))
                .andExpect(jsonPath("$[0].id", is(itemDto.getId()), Long.class));
    }

    @SneakyThrows
    @Test
    void createComment() {
        CommentDto commentDto = CommentDto.builder()
                .text("commentText")
                .build();
        CommentDto commentDtoTest = CommentDto.builder()
                .id(1L)
                .created(LocalDateTime.of(2022, 2, 2, 2, 2, 2))
                .authorName("userName")
                .build();
        when(itemService.saveComment(any(), anyLong(), anyLong())).thenReturn(commentDtoTest);

        mockMvc.perform(MockMvcRequestBuilders.post("/items/1/comment")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentDtoTest.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(commentDtoTest.getText()), String.class))
                .andExpect(jsonPath("$.authorName", is(commentDtoTest.getAuthorName()), String.class))
                .andExpect(jsonPath("$.created",
                        is(commentDtoTest.getCreated().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")))));
    }
}