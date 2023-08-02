package ru.practicum.shareit.booking.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDtoIdAndBooker;
import ru.practicum.shareit.booking.dto.BookingDtoWithStartEndItemId;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingWithItemDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.validation.BookingValidation;
import ru.practicum.shareit.exception.ValidationException400;
import ru.practicum.shareit.exception.ValidationException404;
import ru.practicum.shareit.exception.ValidationException500;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@Slf4j
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
    public BookingWithItemDto save(BookingDtoWithStartEndItemId bookingDtoWithStartEndItemId, int userId) {

        User user = UserMapper.dtoToUser(userService.getUserById(userId));
        ItemDto item1 = itemService.getItem(bookingDtoWithStartEndItemId.getItemId(), userId);
        Item item = ItemMapper.dtoToItem(item1);
        BookingValidation.validateBookingStartEnd(bookingDtoWithStartEndItemId);

        Booking booking = BookingMapper.BookingDtoWithStartEndItemIdTOBooking(bookingDtoWithStartEndItemId);
        booking.setUser(user);
        booking.setItem(item);
        booking.setStatus(BookingStatus.WAITING.name());

        BookingValidation.validateBooking(booking, itemService);
        BookingValidation.validateIfUserBooksHisItems(userId, itemService, booking);

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
                return BookingMapper.bookingToDtoList(bookingRepository.findAllByBookerIdInFuture(userId));
            case "PAST":
                return BookingMapper.bookingToDtoList(bookingRepository.findAllByBookerIdInPast(userId));
            case "CURRENT":
                return BookingMapper.bookingToDtoList(bookingRepository.findAllByBookerIdInCurrent(userId));
            case "WAITING":
                statuses = List.of("WAITING");
                return BookingMapper.bookingToDtoList(bookingRepository.findAllByBookerId(userId, statuses));
            case "REJECTED":
                statuses = List.of("REJECTED");
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
                return BookingMapper.bookingToDtoList(bookingRepository.findAllByOwnerIdInFuture(userId));
            case "PAST":
                return BookingMapper.bookingToDtoList(bookingRepository.findAllByOwnerIdInPast(userId));
            case "CURRENT":
                return BookingMapper.bookingToDtoList(bookingRepository.findAllByOwnerIdInCurrent(userId));
            case "WAITING":
                statuses = List.of("WAITING");
                return BookingMapper.bookingToDtoList(bookingRepository.findAllByOwnerId(userId, statuses));
            case "REJECTED":
                statuses = List.of("REJECTED");
                return BookingMapper.bookingToDtoList(bookingRepository.findAllByOwnerId(userId, statuses));
            default:
                throw new ValidationException500("Unknown state: UNSUPPORTED_STATUS");
        }
    }

    @Override
    public List<BookingDtoIdAndBooker> findNextAndLastBookingByItemId(int itemId, int userId, ItemDto itemDto) {
        List<BookingDtoIdAndBooker> lastAndNextBookings = new ArrayList<>();


        if (bookingRepository.findAllByItemId(itemId).isEmpty())
            return new ArrayList<>();

        int ownerId = itemService.getItemById(itemId).getUser().getId();
        if (ownerId != userId) {
            return new ArrayList<>();
        }
        markLastAndNextBookingsFromCurTime(itemId, lastAndNextBookings);
        return lastAndNextBookings;
    }

    private void markLastAndNextBookingsFromCurTime(int itemId, List<BookingDtoIdAndBooker> lastAndNextBookings) {
        Booking nextBookingByItemId = null;
        try {
            nextBookingByItemId = bookingRepository.findNextBookingByItemId(itemId);
            lastAndNextBookings.add
                    (BookingMapper.bookingToDtoIdAndBooker(nextBookingByItemId));
        } catch (
                Exception e) {
            log.debug("last booking for item " + itemId + " not found");
        }
        if (nextBookingByItemId == null) {
            lastAndNextBookings.add(null);
        }
        Booking lastBookingByItemId = null;
        try {
            lastBookingByItemId = bookingRepository.findLastBookingByItemId(itemId);
            lastAndNextBookings.add
                    (BookingMapper.bookingToDtoIdAndBooker(lastBookingByItemId));
        } catch (Exception e) {
            log.debug("next booking for item " + itemId + " not found");
        }
        if (lastBookingByItemId == null) {

            lastAndNextBookings.add(null);
        }
    }


    @Override
    public List<BookingDtoIdAndBooker> findAllByBookerAndItemIdAndGoodState(int userId, int itemId) {
        return BookingMapper.ListBookingToDtoIdAndBooker(bookingRepository.findAllByBookerAndItemId(userId, itemId));
    }


}
