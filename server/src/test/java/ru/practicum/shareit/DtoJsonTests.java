package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingWithItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class DtoJsonTests {
    @Autowired
    private JacksonTester<Object> json;

    @Test
    void testUserSerialization() throws IOException {
        UserDto rick =
                UserDto.builder().id(1).name("rick").email("ggg@ggg.ru").build();
        JsonContent<Object> result = json.write(rick);
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("rick");
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo("ggg@ggg.ru");

        User user = UserMapper.dtoToUser(rick);
        result = json.write(user);
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("rick");
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo("ggg@ggg.ru");

        rick = UserMapper.userToDto(user);
        result = json.write(rick);
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("rick");
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo("ggg@ggg.ru");
    }
    @Test
    void testItemSerialization() throws IOException {
        ItemDto itemDto =
                ItemDto.builder().id(1).description("ggg").name("gg").available(true).requestId(1).build();
        JsonContent<Object> result = json.write(itemDto);
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("gg");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("ggg");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isTrue();

        Item item = ItemMapper.dtoToItem(itemDto);
        result = json.write(item);
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("gg");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("ggg");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isTrue();

        itemDto = ItemMapper.itemToDto(item);
        result = json.write(itemDto);
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("gg");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("ggg");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isTrue();
    }
    @Test
    void bookingItemSerialization() throws IOException {
        BookingWithItemDto bookingWithItemDto =
                BookingWithItemDto.builder().id(1).status("ACTIVE").build();
        JsonContent<Object> result = json.write(bookingWithItemDto);
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo("ACTIVE");
    }

    @Test
    void requestItemSerialization() throws IOException {
        ItemRequestDto itemRequestDto =
                ItemRequestDto.builder().id(1).description("gg").build();
        JsonContent<Object> result = json.write(itemRequestDto);
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("gg");
    }
}
