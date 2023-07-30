package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.ValidationException400;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final ItemService itemService;
    @PersistenceContext
    EntityManager entityManager;

    @Transactional
    @Override
    public BookingDto save(BookingDto bookingDto, int userId) {
        User booker = UserMapper.dtoToUser(userService.getUserById(userId));
        bookingDto.setBooker(booker);
        Item item = ItemMapper.dtoToItem(itemService.getItem(bookingDto.getItemId(), userId));
        if (!itemService.validateIfUserHasRights(bookingDto.getItemId(), userId))
            throw new ValidationException400("user "+ userId + " has no rights to manage item "
                    + bookingDto.getItemId());
        Booking booking = BookingMapper.dtoToBooking(bookingDto);
        booking.setUser(UserMapper.dtoToUser(userService.getUserById(userId)));
        booking.setItem(ItemMapper.dtoToItem(itemService.getItem(bookingDto.getItemId(), userId)));
        booking.setStatus(BookingStatus.WAITING);
        return BookingMapper.bookingToDto(bookingRepository.save(booking));
    }

    @Override
    @Transactional
    public void deleteAll() {
        bookingRepository.deleteAll();
        entityManager
                .createNativeQuery("ALTER TABLE  BOOKINGS  ALTER COLUMN ID  RESTART WITH 1;")
                .executeUpdate();
    }
}
