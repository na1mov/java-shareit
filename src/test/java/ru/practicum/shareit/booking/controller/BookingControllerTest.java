package ru.practicum.shareit.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequest;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.model.ErrorResponse;
import ru.practicum.shareit.exception.model.MyValidationException;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
@AutoConfigureMockMvc
class BookingControllerTest {
    @MockBean
    BookingService bookingService;
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper mapper;
    private final User userOne = new User(1L, "testNameOne", "testEmailOne@gmail.com");
    private final User userTwo = new User(2L, "testNameTwo", "testEmailTwo@gmail.com");
    private final Item item = new Item(1L, "itemName",
            "itemDescription", true, userOne, null);
    private final BookingRequest bookingRequest = BookingRequest.builder()
            .itemId(item.getId())
            .start(LocalDateTime.now().plusMinutes(8))
            .end(LocalDateTime.now().plusMinutes(16))
            .build();
    private final BookingDto bookingDto = BookingDto.builder()
            .id(item.getId())
            .start(LocalDateTime.now().plusMinutes(8))
            .end(LocalDateTime.now().plusMinutes(16))
            .item(item)
            .booker(userTwo)
            .status(BookingStatus.WAITING)
            .build();


    @SneakyThrows
    @Test
    void save() {
        when(bookingService.save(any(), anyLong())).thenReturn(bookingDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(bookingRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.item.id", is(bookingDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.booker.id", is(bookingDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().toString()), String.class));
    }

    @SneakyThrows
    @Test
    void update() {
        BookingDto bookingDtoUpd = BookingDto.builder()
                .id(item.getId())
                .start(LocalDateTime.now().plusMinutes(8))
                .end(LocalDateTime.now().plusMinutes(16))
                .item(item)
                .booker(userTwo)
                .status(BookingStatus.APPROVED)
                .build();
        when(bookingService.update(anyLong(), anyLong(), any())).thenReturn(bookingDtoUpd);

        mockMvc.perform(MockMvcRequestBuilders.patch("/bookings/1")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("approved", "true")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @SneakyThrows
    @Test
    void updateThrowsMyValidationIfWrongStatus() {
        BookingDto bookingDtoUpd = BookingDto.builder()
                .id(item.getId())
                .start(LocalDateTime.now().plusMinutes(8))
                .end(LocalDateTime.now().plusMinutes(16))
                .item(item)
                .booker(userTwo)
                .status(BookingStatus.APPROVED)
                .build();
        ErrorResponse errorResponse = new ErrorResponse("Ошибка изменения статуса.");

        when(bookingService.update(anyLong(), anyLong(), any()))
                .thenThrow(new MyValidationException("Ошибка изменения статуса."));

        mockMvc.perform(MockMvcRequestBuilders.patch("/bookings/1")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(bookingDtoUpd))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("approved", "true")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is(errorResponse.getError()), String.class));
    }

    @SneakyThrows
    @Test
    void updateThrowsNotFoundIfWrongOwner() {
        BookingDto bookingDtoUpd = BookingDto.builder()
                .id(item.getId())
                .start(LocalDateTime.now().plusMinutes(8))
                .end(LocalDateTime.now().plusMinutes(16))
                .item(item)
                .booker(userOne)
                .status(BookingStatus.WAITING)
                .build();
        ErrorResponse errorResponse = new ErrorResponse(String
                .format("Ошибка доступа. Изменить статус вещи с ID:%d может только её владелец.", bookingDto.getId()));

        when(bookingService.update(anyLong(), anyLong(), any())).thenThrow(new NotFoundException(String
                .format("Ошибка доступа. Изменить статус вещи с ID:%d может только её владелец.", bookingDto.getId())));

        mockMvc.perform(MockMvcRequestBuilders.patch("/bookings/1")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(bookingDtoUpd))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("approved", "true")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is(errorResponse.getError()), String.class));
    }

    @SneakyThrows
    @Test
    void findById() {
        when(bookingService.findByIdAndUserId(anyLong(), anyLong())).thenReturn(bookingDto);

        mockMvc.perform(MockMvcRequestBuilders.get("/bookings/1")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(bookingRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.item.id", is(bookingDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.booker.id", is(bookingDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().toString()), String.class));
    }

    @SneakyThrows
    @Test
    void findAllByState() {
        when(bookingService.findAllByState(any(), anyLong(), any())).thenReturn(List.of(bookingDto));

        mockMvc.perform(MockMvcRequestBuilders.get("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(bookingRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("size", "10")
                        .param("from", "1")
                        .param("state", "ALL")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].item.id", is(bookingDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$[0].booker.id", is(bookingDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$[0].status", is(bookingDto.getStatus().toString()), String.class));
    }

    @SneakyThrows
    @Test
    void findAllByStateWithAnotherSize() {
        when(bookingService.findAllByState(any(), anyLong(), any())).thenReturn(List.of(bookingDto));

        mockMvc.perform(MockMvcRequestBuilders.get("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(bookingRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("size", "5")
                        .param("from", "0")
                        .param("state", "ALL")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].item.id", is(bookingDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$[0].booker.id", is(bookingDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$[0].status", is(bookingDto.getStatus().toString()), String.class));
    }

    @SneakyThrows
    @Test
    void findAllByOwner() {
        when(bookingService.findAllByOwner(any(), anyLong(), any())).thenReturn(List.of(bookingDto));
        mockMvc.perform(MockMvcRequestBuilders.get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(bookingRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("size", "10")
                        .param("from", "1")
                        .param("state", "ALL")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].item.id", is(bookingDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$[0].booker.id", is(bookingDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$[0].status", is(bookingDto.getStatus().toString()), String.class));
    }
}