package ru.practicum.shareit.unit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@WebMvcTest(UserController.class)
public class MvcControllerUserTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService service;
    @Autowired
    ObjectMapper mapper;

    @Test
    public void userCreationTest() throws Exception {
        UserDto rick = UserDto.builder().name("rick").email("ggg@ggg.ru").build();
        when(service.saveUser(rick)).thenReturn(rick);
        MvcResult mvcResult = mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(rick))).andDo(print()).andReturn();
        String contentAsString = mvcResult.getResponse().getContentAsString();
        System.out.println(contentAsString);

        List<UserDto> allUsers = service.getAllUsers();
        System.out.println(allUsers);
    }

}
