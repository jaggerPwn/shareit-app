package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Integer> {
    List<Item> findByUserIdOrderByIdAsc(int userId);
    Page<Item> findByUserIdOrderByIdAsc(Long userId, Pageable pageable);

    List<Item> findByDescriptionContainingIgnoreCaseAndAvailableTrue(String description);
}
