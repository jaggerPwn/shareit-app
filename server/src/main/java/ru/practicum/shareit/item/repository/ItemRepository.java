package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Integer> {
    List<Item> findByUserIdOrderByIdAsc(int userId);

    Page<Item> findByUserIdOrderByIdAsc(int userId, Pageable pageable);

    List<Item> findByDescriptionContainingIgnoreCaseAndAvailableTrue(String description);

    List<Item> findAllByRequestId(int requestId);

    @Modifying
    @Query(value = "ALTER TABLE  ITEMS ALTER COLUMN ID  RESTART WITH 1", nativeQuery = true)
    void setIdToOne();
}
