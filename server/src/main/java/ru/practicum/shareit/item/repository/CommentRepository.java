package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
    List<Comment> findAllByItemId(int itemId);
@Modifying
@Query(value = "ALTER TABLE COMMENTS ALTER COLUMN ID  RESTART WITH 1", nativeQuery = true)
    void setCommetIdToOne();
}
