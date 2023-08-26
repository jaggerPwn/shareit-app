package ru.practicum.shareit.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.user.model.User;

public interface UserRepository extends JpaRepository<User, Integer> {
    boolean existsUserByEmail(String email);

    boolean existsUserById(int id);

    @Modifying
    @Query(value = "ALTER TABLE  USERS  ALTER COLUMN ID  RESTART WITH 1", nativeQuery = true)
    void setUserIdToOne();
}