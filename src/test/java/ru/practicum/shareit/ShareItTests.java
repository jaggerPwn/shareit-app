package ru.practicum.shareit;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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
import ru.practicum.shareit.booking.dto.BookingWithItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Thread.sleep;

@SpringBootTest
class ShareItTests {
    private MockMvc mockMvc;
    ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext wac;

    @BeforeEach
    public void setup() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
        mockMvc.perform(MockMvcRequestBuilders.delete("/items/comments"));
        mockMvc.perform(MockMvcRequestBuilders.delete("/bookings"));
        mockMvc.perform(MockMvcRequestBuilders.delete("/items"));
        mockMvc.perform(MockMvcRequestBuilders.delete("/users"));
        objectMapper = new ObjectMapper();
        //		<dependency>
        //			<groupId>com.fasterxml.jackson.datatype</groupId>
        //			<artifactId>jackson-datatype-jsr310</artifactId>
        //			<version>2.6.0</version>
        //		</dependency>
        //		негативно влияет на прохождение постмен тестов, при этом в идее
        // если раскомментировать и добавить в pom и всё проходит
        //objectMapper.findAndRegisterModules();
    }
    @Test
    public void addUserAndGetUser() throws Exception {
        addTwoUsers();
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/users/1")).andReturn();
        String contentAsString = mvcResult.getResponse().getContentAsString();
        UserDto user = objectMapper.readValue(contentAsString, UserDto.class);
        Assertions.assertEquals(user.getId(), 1);
        Assertions.assertEquals(user.getEmail(), "user@user.com");
    }

    @Test
    public void updateUser() throws Exception {
        addTwoUsers();
        String jsonStr = "{\"name\":\"name updated\"}";
        MvcResult mvcResult2 = mockMvc.perform(MockMvcRequestBuilders.patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonStr.getBytes()))
                .andExpect(MockMvcResultMatchers.status()
                        .isOk())
                .andReturn();
        String contentAsString2 = mvcResult2.getResponse().getContentAsString();
        UserDto user = objectMapper.readValue(contentAsString2, UserDto.class);
        Assertions.assertEquals(user.getName(), "name updated");
    }

    @Test
    public void deleteUser() throws Exception {
        addTwoUsers();
        mockMvc.perform(MockMvcRequestBuilders.delete("/users/1"));
        MvcResult mvcResult2 = mockMvc.perform(MockMvcRequestBuilders.get("/users/1")).andReturn();
        String contentAsString2 = mvcResult2.getResponse().getContentAsString();
        Assertions.assertEquals(contentAsString2, "{\"error\":\"user 1not found\"}");
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


//jackson-datatype-jsr310 негативно влияет на прохождение постмен тестов, при этом в идее
//если раскомментировать всё проходит
//    @Test
//    public void addBookings() throws Exception {
//        addTwoUsers();
//        addTwoItems();
//        addTwoBookings();
//        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/bookings/1")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .header("X-Sharer-User-Id", 2))
//                .andExpect(MockMvcResultMatchers.status().isOk())
//                .andReturn();
//        String contentAsString = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
//        BookingWithItemDto bookingDtos = objectMapper.readValue(contentAsString, BookingWithItemDto.class);
//        Assertions.assertEquals(bookingDtos.getItem().toString(), "Item(id=2, name=Отвертка, description=Аккумуляторная отвертка, available=true)");
//    }
//
//    @Test
//    public void addCommentTest() throws Exception {
//        addTwoUsers();
//        addTwoItems();
//        addTwoBookings();
//        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.patch("/bookings/1?approved=true")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .header("X-Sharer-User-Id", 2))
//                .andExpect(MockMvcResultMatchers.status().isOk())
//                .andReturn();
//        String contentAsString = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
//        BookingWithItemDto bookingDtos = objectMapper.readValue(contentAsString, BookingWithItemDto.class);
//        sleep(4000);
//        addComment();
//        mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/items/2")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .header("X-Sharer-User-Id", 1))
//                .andExpect(MockMvcResultMatchers.status().isOk())
//                .andReturn();
//        contentAsString = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
//        ItemDto itemDto = objectMapper.readValue(contentAsString, ItemDto.class);
//        Assertions.assertEquals(itemDto.getComments().get(0).getText(), "Add comment from user1");
//
//        mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/items/1")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .header("X-Sharer-User-Id", 2))
//                .andExpect(MockMvcResultMatchers.status().isOk())
//                .andReturn();
//        contentAsString = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
//        itemDto = objectMapper.readValue(contentAsString, ItemDto.class);
//        Assertions.assertEquals(itemDto.getComments(), new ArrayList<>());
//    }

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
//
//    private void addTwoBookings() throws Exception {
//        String curTime = LocalDateTime.now().plusSeconds(1).toString();
//        String curTimeplusThreeSec = LocalDateTime.now().plusSeconds(3).toString();
//        String jsonStr =
//                "{\n" +
//                        "    \"itemId\": 2,\n" +
//                        "    \"start\": \"" + curTime + "\",\n" +
//                        "    \"end\": \"" + curTimeplusThreeSec + "\"\n" +
//                        "}";
//        mockMvc.perform(MockMvcRequestBuilders.post("/bookings")
//                .header("X-Sharer-User-Id", 1)
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(jsonStr.getBytes()));
//
//
//        jsonStr = "{\n" +
//                "    \"itemId\": 1,\n" +
//                "    \"start\": \"" + curTime + "\",\n" +
//                "    \"end\": \"" + curTimeplusThreeSec + "\"\n" +
//                "}";
//        mockMvc.perform(MockMvcRequestBuilders.post("/bookings")
//                .header("X-Sharer-User-Id", 2)
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(jsonStr.getBytes()));
//
//    }
//
//    private void addComment() throws Exception {
//        String jsonStr =
//                "{\n" +
//                        "    \"text\": \"Add comment from user1\"\n" +
//                        "}";
//        mockMvc.perform(MockMvcRequestBuilders.post("/items/2/comment")
//                .header("X-Sharer-User-Id", 1)
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(jsonStr.getBytes()));
//        jsonStr =
//                "{\n" +
//                        "    \"text\": \"Add comment from user1\"\n" +
//                        "}";
//        mockMvc.perform(MockMvcRequestBuilders.post("/items/1/comment")
//                .header("X-Sharer-User-Id", 2)
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(jsonStr.getBytes()));
//    }

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
