package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.dto.UserDTO;
import ru.practicum.shareit.user.model.User;

import java.util.Map;

public interface UserRepository {
    Map<Integer, User> findAll();
    UserDTO save(User user);

    UserDTO update(User user, int id);

    UserDTO getUserById(int id);

    void deleteUserById(int id);
}