package ru.practicum.shareit.unit;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.MockitoSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ValidationException409;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.service.UserServiceImpl;
import ru.practicum.shareit.user.validation.UserValidator;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserNarrowUnitMockTests {
    UserRepository mockRepository = Mockito.mock(UserRepository.class);
    UserService userService;
    private MockitoSession session;

    ObjectMapper mapper;
    @PersistenceContext
    EntityManager entityManager;

    @BeforeEach
    void init() {
        session = Mockito.mockitoSession().initMocks(this).startMocking();
        userService = new UserServiceImpl(mockRepository, entityManager);
    }

    @AfterEach
    void finish() {
        session.finishMocking();
    }

    @Test
    void testUserCreation() throws IOException {
        User rick =
                User.builder().name("rick").email("ggg@ggg.ru").build();
        Mockito.when(mockRepository.findAll())
                .thenReturn(List.of(rick));

        List<UserDto> allUsers = userService.getAllUsers();

        assertThat(allUsers.get(0), equalTo(UserMapper.userToDto(rick)));
        Mockito.verify(mockRepository, Mockito.times(1)).findAll();
    }

    @Test
    void TestMailExists(){
        User rick = User.builder().id(1).name("rick").email("ggg@ggg.ru").build();
        User rick2 = User.builder().id(2).name("rick").email("ggg@ggg.ru").build();
        Mockito.when(mockRepository.findById(Mockito.anyInt())).thenReturn(Optional.ofNullable(rick));
        Mockito.when(mockRepository.existsUserByEmail(Mockito.anyString())).thenReturn(true);
        Assertions.assertThrows(ValidationException409.class, () -> UserValidator.validateForDuplicateMail(UserMapper.userToDto(rick2), mockRepository));
        Mockito.verify(mockRepository, Mockito.times(1)).existsUserByEmail(Mockito.anyString());
    }

}
