package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.ValidationException404;
import ru.practicum.shareit.exception.ValidationException409;
import ru.practicum.shareit.user.dto.UserDTO;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.HashMap;
import java.util.Map;

@Repository
public class UserRepositoryImpl implements UserRepository {
    private final Map<Integer, User> users = new HashMap<>();
    private int nextId;

    public UserRepositoryImpl() {
        nextId = 0;
    }

    @Override
    public Map<Integer, User> findAll() {
        return users;
    }

    @Override
    public UserDTO save(User user) {
        user.setId(getId());
        users.put(user.getId(), user);
        return UserMapper.userToDto(user);
    }

    @Override
    public UserDTO update(User user, int id) {
        User userFromBase = users.get(id);
        if (user.getName() == null)
            user.setName(userFromBase.getName());
        if (user.getEmail() == null)
            user.setEmail(userFromBase.getEmail());
        users.forEach((key, value) -> {
            if (key != id && value.getEmail().equals(user.getEmail()))
                throw new ValidationException409("Email already in base");
        });
        users.put(id, user);
        return UserMapper.userToDto(users.get(id));
    }

    @Override
    public UserDTO getUserById(int id) {
        try {
            return UserMapper.userToDto(users.get(id));
        } catch (NullPointerException e) {
            throw new ValidationException404("User " + id + "not found");
        }
    }

    @Override
    public void deleteUserById(int id) {
        users.remove(id);
    }

    @Override
    public void deleteUsers() {
        users.clear();
        nextId = 0;
    }

    private int getId() {
        nextId++;
        return nextId;
    }
}
