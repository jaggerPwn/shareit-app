package ru.practicum.shareit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

public class MockTests {

    @Test
    void testUserCreation() {
     UserService mockUserService = Mockito.mock(UserService.class);
        UserDto rick = mockUserService.saveUser(UserDto.builder().name("rick").email("ggg@ggg.ru").build());
        Assertions.assertEquals(mockUserService.getUserById(1), rick);
    }

}
