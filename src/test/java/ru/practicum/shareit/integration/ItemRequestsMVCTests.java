package ru.practicum.shareit.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest
public class ItemRequestsMVCTests {
    private MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;

    JacksonTester<Object> json;
    @Autowired
    private WebApplicationContext wac;

    @BeforeEach
    public void setup() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
        mockMvc.perform(MockMvcRequestBuilders.delete("/requests/"));
        mockMvc.perform(MockMvcRequestBuilders.delete("/items/comments/"));
        mockMvc.perform(MockMvcRequestBuilders.delete("/bookings"));
        mockMvc.perform(MockMvcRequestBuilders.delete("/items"));
        mockMvc.perform(MockMvcRequestBuilders.delete("/users"));
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        JacksonTester.initFields(this, objectMapper);
    }

    @AfterEach
    public void tearDown() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/requests/"));
        mockMvc.perform(MockMvcRequestBuilders.delete("/items/comments/"));
        mockMvc.perform(MockMvcRequestBuilders.delete("/bookings"));
        mockMvc.perform(MockMvcRequestBuilders.delete("/items"));
        mockMvc.perform(MockMvcRequestBuilders.delete("/users"));
    }

    @Test
    public void addAndGetRequests() throws Exception {
        addTwoUsers();
        addTwoItems();
        addTwoBookings();
        addTwoRequests();
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/requests/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        String contentAsString = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
        ItemRequestDto itemRequestDto = objectMapper.readValue(contentAsString, ItemRequestDto.class);

        Assertions.assertEquals(itemRequestDto.getId(), 1);
        Assertions.assertEquals(itemRequestDto.getItems(), List.of());
        Assertions.assertEquals(itemRequestDto.getDescription(), "need ammo");
        Assertions.assertEquals(itemRequestDto.getCreated().getMinute(), LocalDateTime.now().getMinute());
    }


    @Test
    public void addItemResponseTorequest() throws Exception {
        addTwoUsers();
        addTwoItems();
        addTwoBookings();
        addTwoRequests();
        String itemStr = "{\n" +
                "    \"name\": \"ammo\",\n" +
                "    \"description\": \"good ammo\",\n" +
                "    \"available\": true,\n" +
                "    \"requestId\": 1\n" +
                "}";
        mockMvc.perform(MockMvcRequestBuilders.post("/items")
                        .content(itemStr)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 2))

                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/items/3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        String contentAsString = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
        ItemDto itemDto = objectMapper.readValue(contentAsString, ItemDto.class);
        Assertions.assertEquals(itemDto.getRequestId(), 1);
         mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/items/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        contentAsString = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
        itemDto = objectMapper.readValue(contentAsString, ItemDto.class);
        Assertions.assertNull(itemDto.getRequestId());
    }

    private void addTwoRequests() throws Exception {
        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .description("need ammo")
                .build();
        String jsonStr = json.write(itemRequestDto).getJson();
        mockMvc.perform(MockMvcRequestBuilders.post("/requests")
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id", 1)
                .content(jsonStr.getBytes())).andExpect(MockMvcResultMatchers.status().isOk());
        itemRequestDto = ItemRequestDto.builder()
                .description("need guns")
                .build();
        jsonStr = json.write(itemRequestDto).getJson();
        mockMvc.perform(MockMvcRequestBuilders.post("/requests")
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id", 1)
                .content(jsonStr.getBytes())).andExpect(MockMvcResultMatchers.status().isOk());
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

    private void addTwoBookings() throws Exception {
        String curTime = LocalDateTime.now().plusSeconds(1).toString();
        String curTimeplusThreeSec = LocalDateTime.now().plusSeconds(3).toString();
        String jsonStr =
                "{\n" +
                        "    \"itemId\": 2,\n" +
                        "    \"start\": \"" + curTime + "\",\n" +
                        "    \"end\": \"" + curTimeplusThreeSec + "\"\n" +
                        "}";
        mockMvc.perform(MockMvcRequestBuilders.post("/bookings")
                .header("X-Sharer-User-Id", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonStr.getBytes()));


        jsonStr = "{\n" +
                "    \"itemId\": 1,\n" +
                "    \"start\": \"" + curTime + "\",\n" +
                "    \"end\": \"" + curTimeplusThreeSec + "\"\n" +
                "}";
        mockMvc.perform(MockMvcRequestBuilders.post("/bookings")
                .header("X-Sharer-User-Id", 2)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonStr.getBytes()));

    }

    private void addComment() throws Exception {
        String jsonStr =
                "{\n" +
                        "    \"text\": \"Add comment from user1\"\n" +
                        "}";
        mockMvc.perform(MockMvcRequestBuilders.post("/items/2/comment")
                .header("X-Sharer-User-Id", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonStr.getBytes()));
        jsonStr =
                "{\n" +
                        "    \"text\": \"Add comment from user1\"\n" +
                        "}";
        mockMvc.perform(MockMvcRequestBuilders.post("/items/1/comment")
                .header("X-Sharer-User-Id", 2)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonStr.getBytes()));
    }
}
