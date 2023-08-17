package ru.practicum.shareit.integration;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ru.practicum.shareit.item.dto.ItemDto;

import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.List;

@SpringBootTest
//@WebMvcTest
class ItemMVCTests {

    private MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext wac;

    @BeforeEach
    public void setup() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
        mockMvc.perform(MockMvcRequestBuilders.delete("/items"));
        mockMvc.perform(MockMvcRequestBuilders.delete("/users"));
        objectMapper = new ObjectMapper();
    }

    @AfterEach
    public void tearDown() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/items"));
        mockMvc.perform(MockMvcRequestBuilders.delete("/users"));
    }

    @Test
    public void itemAddAndGet() throws Exception {
        addTwoUsers();
        addTwoItems();
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/items/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 2))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        String contentAsString = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
        ItemDto item = objectMapper.readValue(contentAsString, ItemDto.class);
        Assertions.assertEquals(item.getId(), 1);
        Assertions.assertEquals(item.getName(), "Дрель");
    }


    @Test
    public void itemSearch() throws Exception {
        addTwoUsers();
        addTwoItems();
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/items/search/?text={thing}",
                                "аккУМУляторная")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))

                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        String contentAsString = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
        List<ItemDto> itemDtos = objectMapper.readValue(contentAsString, new TypeReference<>() {
            @Override
            public Type getType() {
                return super.getType();
            }
        });
        Assertions.assertEquals(itemDtos.size(), 1);
        Assertions.assertEquals(itemDtos.get(0).getName(), "Отвертка");
    }

    private void addTwoItems() throws Exception {
        String jsonStr =
                "{\n" +
                        "    \"name\": \"Дрель\",\n" +
                        "    \"description\": \"Простая дрель\",\n" +
                        "    \"available\": true\n" +
                        "}";
        mockMvc.perform(MockMvcRequestBuilders.post("/items")
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id", 1)
                .content(jsonStr.getBytes())).andExpect(MockMvcResultMatchers.status().isOk());
        jsonStr =
                "{\n" +
                        "    \"name\": \"Отвертка\",\n" +
                        "    \"description\": \"Аккумуляторная отвертка\",\n" +
                        "    \"available\": true\n" +
                        "}";
        mockMvc.perform(MockMvcRequestBuilders.post("/items")
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id", 2)
                .content(jsonStr.getBytes())).andExpect(MockMvcResultMatchers.status().isOk());

    }

    private void addTwoUsers() throws Exception {
        String jsonStr =
                "{\n" +
                        "    \"name\": \"user\",\n" +
                        "    \"email\": \"user@user.com\"\n" +
                        "}";
        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonStr.getBytes()));
        jsonStr = "{\n" +
                "    \"name\": \"user2\",\n" +
                "    \"email\": \"user2@user.com\"\n" +
                "}";
        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonStr.getBytes()));

    }

}
