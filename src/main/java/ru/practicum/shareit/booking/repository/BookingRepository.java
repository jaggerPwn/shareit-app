package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Integer> {
    Page<Booking> findAllByUserIdOrderByIdDesc(int userId, Pageable pageable);

    Page<Booking> findAllByUserIdAndStatusOrderByIdDesc(int userId, String status, Pageable pageable);

    @Query(" select b " +
            "from Booking b " +
            "JOIN b.item as i " +
            "JOIN i.user as u " +
            "where u.id = ?1 " +
            "and b.status in ?2 " +
            "order by b.start desc")
    Page<Booking> findAllByOwnerId(int userId, String status, Pageable pageable);

    @Query(" select b " +
            "from Booking b " +
            "JOIN b.item i " +
            "JOIN i.user u " +
            "where u.id = ?1 " +
            "order by b.start desc")
    Page<Booking> findAllByOwnerId(int userId, Pageable pageable);

    @Query("select b1 " +
            "from Booking as b1 " +
            "where b1.start in " +
            "(select (min(b.start)) as s " +
            "from Booking b " +
            "JOIN b.item i " +
            "where i.id = ?1 " +
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
            "where i.id = ?1 " +
            "and b.start < current_timestamp " +
            "and b.status = 'APPROVED')"
    )
    Booking findLastBookingByItemId(int itemId);

    @Query(" select b " +
            "from Booking as b " +
            "JOIN b.item i " +
            "JOIN b.user u " +
            "where u.id = :booker_id " +
            "and i.id = :item_id " +
            "and b.status = 'APPROVED' " +
            "and b.start < current_timestamp " +
            "order by b.start desc")
    List<Booking> findAllByBookerAndItemId(@Param("booker_id") int userId, @Param("item_id") int itemId);

    @Query(" select b " +
            "from Booking as b " +
            "JOIN b.user u " +
            "where u.id = :booker_id " +
            "and b.start >= current_timestamp " +
            "order by b.start desc")
    Page<Booking> findAllByBookerIdInFuture(@Param("booker_id") int userId, Pageable pageable);

    @Query(" select b " +
            "from Booking as b " +
            "JOIN b.user u " +
            "where u.id = :booker_id " +
            "and b.end <= current_timestamp " +
            "order by b.start desc")
    Page<Booking> findAllByBookerIdInPast(@Param("booker_id") int userId, Pageable pageable);

    @Query(" select b " +
            "from Booking as b " +
            "JOIN b.user u " +
            "where u.id = :booker_id " +
            "and b.start <= current_timestamp " +
            "and b.end >= current_timestamp " +
            "order by b.start desc")
    Page<Booking> findAllByBookerIdInCurrent(@Param("booker_id") int userId, Pageable pageable);

    @Query(" select b " +
            "from Booking b " +
            "JOIN b.item i " +
            "JOIN i.user u " +
            "where u.id = :owner_id " +
            "and b.start >= current_timestamp " +
            "order by b.start desc")
    Page<Booking> findAllByOwnerIdInFuture(@Param("owner_id") int userId, Pageable pageable);

    @Query(" select b " +
            "from Booking b " +
            "JOIN b.item i " +
            "JOIN i.user u " +
            "where u.id = :owner_id " +
            "and b.end <= current_timestamp " +
            "order by b.start desc")
    Page<Booking> findAllByOwnerIdInPast(@Param("owner_id") int userId, Pageable pageable);

    @Query(" select b " +
            "from Booking b " +
            "JOIN b.item i " +
            "JOIN i.user u " +
            "where u.id = :owner_id " +
            "and b.start <= current_timestamp " +
            "and b.end >= current_timestamp " +
            "order by b.start desc")
    Page<Booking> findAllByOwnerIdInCurrent(@Param("owner_id") int userId, Pageable pageable);

    List<Booking> findAllByItemId(int itemId);

    @Modifying
    @Query(value = "ALTER TABLE  BOOKINGS  ALTER COLUMN ID  RESTART WITH 1", nativeQuery = true)
    void setbookingsIdToOne();
}
