package ru.practicum.shareit.integration;

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
import ru.practicum.shareit.user.dto.UserDto;

@SpringBootTest
//@WebMvcTest
class UserMVCTests {

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
