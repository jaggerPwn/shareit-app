package ru.practicum.shareit.request.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Integer> {

    Page<ItemRequest> findAllByUserIdNotOrderByIdAsc(int userId, Pageable pageable);

    List<ItemRequest> findAllByUserIdOrderByIdAsc(int userId);

    List<ItemRequest> findAllByUserId(int userId);

    @Modifying
    @Query(value = "ALTER table REQUESTS ALTER COLUMN ID  RESTART WITH 1", nativeQuery = true)
    void alterTableSetFromBeginig();
}
