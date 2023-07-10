package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDTO;

import java.util.List;

public interface UserService {
    List<UserDTO> getAllUsers();

    UserDTO saveUser(UserDTO user);

    UserDTO update(UserDTO userDTO, int id);

    UserDTO getUserById(int id);

    void deleteUserById(int id);
}