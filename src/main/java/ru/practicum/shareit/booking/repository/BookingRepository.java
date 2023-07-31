package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Integer> {
    @Query(" select b " +
            "from Booking b " +
            "JOIN FETCH b.item " +
            "JOIN FETCH b.user " +
            "where b.id = ?1")
    Optional<Booking> findById(int bookingId);

    @Query(" select b " +
            "from Booking b " +
            "JOIN FETCH b.item " +
            "JOIN FETCH b.user as u " +
            "where u.id = ?1 " +
            "order by b.start desc")
    List<Booking> findAllByBookerId(int userId);

    @Query(" select b " +
            "from Booking b " +
            "JOIN FETCH b.item " +
            "JOIN FETCH b.user as u " +
            "where u.id = ?1 " +
            "and b.status in ?2 " +
            "order by b.start desc")
    List<Booking> findAllByBookerId(int userId, List<String> status);

    @Query(" select b " +
            "from Booking b " +
            "JOIN FETCH b.item as i " +
            "JOIN FETCH i.user as u " +
            "where u.id = ?1 " +
            "and b.status in ?2 " +
            "order by b.start desc")
    List<Booking> findAllByOwnerId(int userId, List<String> status);

    @Query(" select b " +
            "from Booking b " +
            "JOIN FETCH b.item i " +
            "JOIN FETCH i.user u " +
            "where u.id = ?1 " +
            "order by b.start desc")
    List<Booking> findAllByOwnerId(int userId);

    @Query("select b1 " +
            "from Booking as b1 " +
            "where b1.start in " +
            "(select (max(b.start)) as s " +
            "from Booking b " +
            "JOIN b.item i " +
            "where i.id  = ?1 and b.start < current_timestamp and b.status = 'APPROVED')"
    )
    Booking findNextBookingByItemId(int itemId);
}
