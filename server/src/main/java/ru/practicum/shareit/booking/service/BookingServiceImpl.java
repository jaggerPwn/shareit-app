package ru.practicum.shareit.booking.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

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
    }

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
        bookingRepository.setbookingsIdToOne();
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
    public List<BookingWithItemDto> findAllByBookerId(int userId, String status, Integer size, Integer from) {
        BookingValidation.validateIfUserExists(userId, userService);

        List<BookingWithItemDto> bookingsList;
        int page;
        if (size != null && from != null) {
            if (size < 0 || from < 0) throw new ValidationException400("from or size can not be negative");
            page = from / size;
            Page<Booking> bookingsPage = getBookerBookingDtos(userId, status, size, page);
            bookingsList = BookingMapper.bookingToDtoList(bookingsPage.stream().collect(Collectors.toList()));
        } else {
            size = 100;
            page = 0;
            Page<Booking> bookingsPage = getBookerBookingDtos(userId, status, size, page);
            bookingsList = BookingMapper.bookingToDtoList(bookingsPage.stream().collect(Collectors.toList()));
        }
        return bookingsList;
    }

    private Pageable createPageRequestUsing(int page, int size) {
        return PageRequest.of(page, size);
    }

    private Page<Booking> getBookerBookingDtos(int userId, String status, Integer size, Integer from) {
        Pageable pageRequest = createPageRequestUsing(from, size);

        String statuses;
        if (status == null) status = "All";
        switch (status) {
            case "ALL":
                return bookingRepository.findAllByUserIdOrderByIdDesc(userId,
                        pageRequest);
            case "FUTURE":
                return bookingRepository.findAllByBookerIdInFuture(userId,
                        pageRequest);
            case "PAST":
                return bookingRepository.findAllByBookerIdInPast(userId,
                        pageRequest);
            case "CURRENT":
                return bookingRepository.findAllByBookerIdInCurrent(userId,
                        pageRequest);
            case "WAITING":
                statuses = "WAITING";
                return bookingRepository.findAllByUserIdAndStatusOrderByIdDesc(userId,
                        statuses, pageRequest);
            case "REJECTED":
                statuses = "REJECTED";
                return bookingRepository.findAllByUserIdAndStatusOrderByIdDesc(userId,
                        statuses, pageRequest);

            default:
                throw new ValidationException500("Unknown state: UNSUPPORTED_STATUS");
        }
    }

    @Override
    public List<BookingWithItemDto> findAllByOwnerId(int userId, String status, Integer size, Integer from) {
        BookingValidation.validateIfUserExists(userId, userService);
        List<BookingWithItemDto> bookingsList;
        int page;

        if (size != null && from != null) {
            if (size < 0 || from < 0) throw new ValidationException400("from or size can not be negative");
            page = from / size;
            Page<Booking> bookingsPage = getOwnerBookingsDto(userId, status, size, page);
            bookingsList = BookingMapper.bookingToDtoList(bookingsPage.stream().collect(Collectors.toList()));
        } else {
            size = 100;
            page = 0;
            Page<Booking> bookingsPage = getOwnerBookingsDto(userId, status, size, page);
            bookingsList = BookingMapper.bookingToDtoList(bookingsPage.stream().collect(Collectors.toList()));
        }
        return bookingsList;
    }

    private Page<Booking> getOwnerBookingsDto(int userId, String status, Integer size, Integer from) {
        String statuses;
        Pageable pageRequest = createPageRequestUsing(from, size);
        switch (status) {
            case "ALL":
                return bookingRepository.findAllByOwnerId(userId, pageRequest);
            case "FUTURE":
                return bookingRepository.findAllByOwnerIdInFuture(userId, pageRequest);
            case "PAST":
                return bookingRepository.findAllByOwnerIdInPast(userId, pageRequest);
            case "CURRENT":
                return bookingRepository.findAllByOwnerIdInCurrent(userId, pageRequest);
            case "WAITING":
                statuses = "WAITING";
                return bookingRepository.findAllByOwnerId(userId, statuses, pageRequest);
            case "REJECTED":
                statuses = "REJECTED";
                return bookingRepository.findAllByOwnerId(userId, statuses, pageRequest);
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
