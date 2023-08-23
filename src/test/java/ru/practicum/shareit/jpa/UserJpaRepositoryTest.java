package ru.practicum.shareit.jpa;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;

@DataJpaTest
public class UserJpaRepositoryTest {
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;
    private User rick;
    private Item item;
    private Booking booking;

    @BeforeEach
    void setup() {
        rick = User.builder()
                .id(1)
                .name("rick")
                .email("ggg@ggg.ru")
                .build();
        item = Item.builder()
                .id(1)
                .user(rick)
                .available(true)
                .description("ggg")
                .name("gg")
                .build();
        booking = Booking.builder()
                .id(1)
                .user(rick)
                .item(item)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now()
                        .plusDays(1))
                .status("WAITING")
                .build();
    }

    @Test
    void testCustomQuery() {
        User save = userRepository.save(rick);
        Assertions.assertEquals(rick, save);
        Item save1 = itemRepository.save(item);
        Assertions.assertEquals(save1, item);
        Booking save2 = bookingRepository.save(booking);
        Page<Booking> allByUserIdOrderByIdDesc =
                bookingRepository.findAllByUserIdOrderByIdDesc(1, PageRequest.of(0, 1));
        Assertions.assertEquals(allByUserIdOrderByIdDesc.getTotalElements(), 1);
    }
}
