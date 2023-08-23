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
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exception.ValidationException409;
import ru.practicum.shareit.exception.ValidationException500;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.service.ItemServiceImpl;
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
import java.util.Optional;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingNarrowUnitMockTests {
    UserRepository mockUserRepository = Mockito.mock(UserRepository.class);
    ItemRepository mockItemRepository = Mockito.mock(ItemRepository.class);
    BookingRepository mockBookingRepository = Mockito.mock(BookingRepository.class);
    CommentRepository mockCommentRepository = Mockito.mock(CommentRepository.class);
    UserService userService;
    ItemService itemService;
    BookingService bookingService;
    private MockitoSession session;
    @Autowired
    private JacksonTester<UserDto> json;

    ObjectMapper mapper;
    @PersistenceContext
    EntityManager entityManager;

    @BeforeEach
    void init() {
        session = Mockito.mockitoSession().initMocks(this).startMocking();
        userService = new UserServiceImpl(mockUserRepository);
        itemService = new ItemServiceImpl(mockItemRepository, mockCommentRepository, userService, bookingService);
        bookingService = new BookingServiceImpl(mockBookingRepository, userService, itemService,entityManager);
    }

    @AfterEach
    void finish() {
        session.finishMocking();
    }

    @Test
    void testUserCreation() throws IOException {
        User rick =
                User.builder().id(1).name("rick").email("ggg@ggg.ru").build();
        Mockito.when(mockUserRepository.findById(Mockito.anyInt())).thenReturn(Optional.ofNullable(rick));
        Assertions.assertThrows(ValidationException500.class,
                () -> bookingService.findAllByBookerId(1, "NONE", 1, 0));
        Mockito.verify(mockUserRepository, Mockito.times(1)).findById(Mockito.any());

    }

    @Test
    void TestMailExists() {
        User rick = User.builder().id(1).name("rick").email("ggg@ggg.ru").build();
        User rick2 = User.builder().id(2).name("rick").email("ggg@ggg.ru").build();
        Mockito.when(mockUserRepository.findById(Mockito.anyInt())).thenReturn(Optional.ofNullable(rick));
        Mockito.when(mockUserRepository.existsUserByEmail(Mockito.anyString())).thenReturn(true);
        Assertions.assertThrows(ValidationException409.class, () -> UserValidator.validateForDuplicateMail(UserMapper.userToDto(rick2), mockUserRepository));
        Mockito.verify(mockUserRepository, Mockito.times(1)).existsUserByEmail(Mockito.anyString());
    }

}
