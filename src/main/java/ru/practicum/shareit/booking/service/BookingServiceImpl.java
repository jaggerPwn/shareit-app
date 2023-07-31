package ru.practicum.shareit.booking.service;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDtoIdAndBooker;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingWithItemDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.validation.BookingValidation;
import ru.practicum.shareit.exception.ValidationException400;
import ru.practicum.shareit.exception.ValidationException404;
import ru.practicum.shareit.exception.ValidationException500;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.NoSuchElementException;

@Service

public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final ItemService itemService;

    public BookingServiceImpl(BookingRepository bookingRepository, UserService userService,
                              @Lazy ItemService itemService, EntityManager entityManager) {
        this.bookingRepository = bookingRepository;
        this.userService = userService;
        this.itemService = itemService;
        this.entityManager = entityManager;
    }

    @PersistenceContext
    EntityManager entityManager;

    @Transactional
    @Override
    public BookingWithItemDto save(BookingWithItemDto bookingWithItemDto, int userId) {

        BookingValidation.validateBooking(bookingWithItemDto, itemService);

        BookingValidation.validateIfUserBooksHisItems(userId, itemService, bookingWithItemDto);
        User user = UserMapper.dtoToUser(userService.getUserById(userId));
        bookingWithItemDto.setBooker(user);

        Item item = ItemMapper.dtoToItem(itemService.getItem(bookingWithItemDto.getItem().getId(), userId));


        Booking booking = BookingMapper.dtoWithItemToBooking(bookingWithItemDto);
        booking.setUser(user);
        booking.setItem(item);
        booking.setStatus(BookingStatus.WAITING.name());


        Booking savedBooking = bookingRepository.save(booking);
        return BookingMapper.bookingToDtoWithItem(savedBooking);
    }

    @Override
    @Transactional
    public void deleteAll() {
        bookingRepository.deleteAll();
        entityManager
                .createNativeQuery("ALTER TABLE  BOOKINGS  ALTER COLUMN ID  RESTART WITH 1;")
                .executeUpdate();
    }

    @Override
    @Transactional
    public BookingWithItemDto approveBooking(int bookingId, boolean accept, int userId) {

        //BookingValidation.validateUserAuthorization(bookingId, userId, bookingRepository);
        Booking booking = bookingRepository.findById(bookingId).get();
        User owner = booking.getItem().getUser();
        User booker = booking.getUser();
        String status = booking.getStatus();

        if (userId == owner.getId()) {
            if (status.equals(BookingStatus.WAITING.name()) && accept) {
                booking.setStatus(BookingStatus.APPROVED.name());
                bookingRepository.save(booking);
                return BookingMapper.bookingToDtoWithItem(booking);
            } else if (status.equals(BookingStatus.APPROVED.name()) && accept) {
                throw new ValidationException400("Status is already : " + status);
            } else if (status.equals(BookingStatus.WAITING.name())) {
                booking.setStatus(BookingStatus.REJECTED.name());
                bookingRepository.save(booking);
                return BookingMapper.bookingToDtoWithItem(booking);
            } else if (status.equals(BookingStatus.REJECTED.name()) && !accept) {
                throw new ValidationException400("Status is already : " + status);
            }
        }
        if (userId == booker.getId()) {
            if (status.equals(BookingStatus.WAITING.name()) && !accept) {
                booking.setStatus(BookingStatus.CANCELED.name());
                bookingRepository.save(booking);
                return BookingMapper.bookingToDtoWithItem(booking);
            } else throw new ValidationException404("Unknown status for " + bookingId);
        }
        throw new ValidationException404("User " + userId +
                "cant accept booking " + bookingId);
    }


    @Override
    public BookingWithItemDto findById(int bookingId, int userId) {
        BookingValidation.validateUserAuthorization(bookingId, userId, bookingRepository);
        try {
            return BookingMapper.bookingToDtoWithItem(bookingRepository.findById(bookingId).get());
        } catch (NoSuchElementException e) {
            throw new ValidationException404("No such booking " + bookingId);
        }
    }

    @Override
    public List<BookingWithItemDto> findAllByBookerId(int userId, String status) {
        BookingValidation.validateIfUserExists(userId, userService);

        List<String> statuses;
        if (status == null) status = "All";
        switch (status) {
            case "ALL":
                return BookingMapper.bookingToDtoList(bookingRepository.findAllByBookerId(userId));
            case "FUTURE":
                statuses = List.of("WAITING", "APPROVED");
                return BookingMapper.bookingToDtoList(bookingRepository.findAllByBookerId(userId, statuses));
            case "PAST":
                statuses = List.of("REJECTED", "CANCELED");
                return BookingMapper.bookingToDtoList(bookingRepository.findAllByBookerId(userId, statuses));
            case "REJECTED":
                statuses = List.of("REJECTED");
                return BookingMapper.bookingToDtoList(bookingRepository.findAllByBookerId(userId, statuses));
            case "WAITING":
                statuses = List.of("WAITING");
                return BookingMapper.bookingToDtoList(bookingRepository.findAllByBookerId(userId, statuses));
            default:
                throw new ValidationException500("Unknown state: UNSUPPORTED_STATUS");
        }
    }

    @Override
    public List<BookingWithItemDto> findAllByOwnerId(int userId, String status) {
        BookingValidation.validateIfUserExists(userId, userService);
        List<String> statuses;
        switch (status) {
            case "ALL":
                return BookingMapper.bookingToDtoList(bookingRepository.findAllByOwnerId(userId));
            case "FUTURE":
                statuses = List.of("WAITING", "APPROVED");
                return BookingMapper.bookingToDtoList(bookingRepository.findAllByOwnerId(userId, statuses));
            case "PAST":
                statuses = List.of("REJECTED", "CANCELED");
                return BookingMapper.bookingToDtoList(bookingRepository.findAllByOwnerId(userId, statuses));
            case "REJECTED":
                statuses = List.of("REJECTED");
                return BookingMapper.bookingToDtoList(bookingRepository.findAllByOwnerId(userId, statuses));
            case "WAITING":
                statuses = List.of("WAITING");
                return BookingMapper.bookingToDtoList(bookingRepository.findAllByOwnerId(userId, statuses));
            default:
                throw new ValidationException500("Unknown state: UNSUPPORTED_STATUS");
        }
    }

    @Override
    public BookingDtoIdAndBooker findNextBookingByItemId(int itemId) {
        return BookingMapper.bookingToDtoIdAndBooker(bookingRepository.findNextBookingByItemId(itemId));
    }

}