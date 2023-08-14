package ru.practicum.shareit.request.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Integer> {
    List<ItemRequest> findAllByUserIdNotOrderByIdAsc(int userId);
    Page<ItemRequest> findAllByUserIdNotOrderByIdAsc(int userId, Pageable pageable);
    List<ItemRequest> findAllByUserIdOrderByIdAsc(int userId);

    List<ItemRequest> findAllByUserId(int userId);
}
