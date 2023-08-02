package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Integer> {
    @Query(" select b " +
            "from Booking b " +
            "JOIN FETCH b.item i " +
            "JOIN FETCH b.user u " +
            "JOIN FETCH i.user " +
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
            "(select (min(b.start)) as s " +
            "from Booking b " +
            "JOIN b.item i " +
            "where i.id  = ?1 " +
            "and b.start >= current_timestamp " +
            "and b.status = 'APPROVED')"
    )
    Booking findNextBookingByItemId(int itemId);

    @Query("select b1 " +
            "from Booking as b1 " +
            "where b1.end in " +
            "(select (max(b.end)) as s " +
            "from Booking b " +
            "JOIN b.item i " +
            "where i.id  = ?1 " +
            "and b.start < current_timestamp " +
            "and b.status = 'APPROVED')"
    )
    Booking findLastBookingByItemId(int itemId);

    @Query(" select b " +
            "from Booking as b " +
            "JOIN  b.item i " +
            "JOIN  b.user u " +
            "where u.id =  :booker_id " +
            "and i.id = :item_id " +
            "and b.status = 'APPROVED' " +
            "and b.start < current_timestamp " +
            "order by b.start desc")
    List<Booking> findAllByBookerAndItemId(@Param("booker_id") int userId, @Param("item_id") int itemId);

    @Query(" select b " +
            "from Booking as b " +
            "JOIN  b.user u " +
            "where u.id =  :booker_id " +
            "and b.start >= current_timestamp " +
            "order by b.start desc")
    List<Booking> findAllByBookerIdInFuture(@Param("booker_id") int userId);

    @Query(" select b " +
            "from Booking as b " +
            "JOIN  b.user u " +
            "where u.id =  :booker_id " +
            "and b.end <= current_timestamp " +
            "order by b.start desc")
    List<Booking> findAllByBookerIdInPast(@Param("booker_id") int userId);

    @Query(" select b " +
            "from Booking as b " +
            "JOIN  b.user u " +
            "where u.id =  :booker_id " +
            "and b.start <= current_timestamp " +
            "and b.end >= current_timestamp " +
            "order by b.start desc")
    List<Booking> findAllByBookerIdInCurrent(@Param("booker_id") int userId);

    @Query(" select b " +
            "from Booking b " +
            "JOIN FETCH b.item i " +
            "JOIN FETCH i.user u " +
            "where u.id = :owner_id " +
            "and b.start >= current_timestamp " +
            "order by b.start desc")
    List<Booking> findAllByOwnerIdInFuture(@Param("owner_id") int userId);

    @Query(" select b " +
            "from Booking b " +
            "JOIN FETCH b.item i " +
            "JOIN FETCH i.user u " +
            "where u.id = :owner_id " +
            "and b.end <= current_timestamp " +
            "order by b.start desc")
    List<Booking> findAllByOwnerIdInPast(@Param("owner_id") int userId);

    @Query(" select b " +
            "from Booking b " +
            "JOIN FETCH b.item i " +
            "JOIN FETCH i.user u " +
            "where u.id = :owner_id " +
            "and b.start <= current_timestamp " +
            "and b.end >= current_timestamp " +
            "order by b.start desc")
    List<Booking> findAllByOwnerIdInCurrent(@Param("owner_id") int userId);

    List<Booking> findAllByItemId(int itemId);

//    not working in TZ14 for existing POSTMAN tests
//    @Query("select b " +
//            "from Booking b " +
//            "where b.end > ?1 " +
//            "and b.start < ?2 " +
//            "and b.status = 'APPROVED'")
//    Booking findIntersections(LocalDateTime startDate, LocalDateTime endDate);
}
