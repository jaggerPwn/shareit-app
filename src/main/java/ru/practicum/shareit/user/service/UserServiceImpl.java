package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDTO;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.validation.UserValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    @Autowired
    private final UserRepository repository;

    @Override
    public List<UserDTO> getAllUsers() {
        Map<Integer, User> all = repository.findAll();
        List<User> collectUsers = new ArrayList<>(all.values());
        return UserMapper.userToDtoList(collectUsers);
    }

    @Override
    public UserDTO saveUser(UserDTO userDTO) {
        User user = UserMapper.dtoToUser(userDTO);
        UserValidator.validate(userDTO, getAllUsers());
        return repository.save(user);
    }

    @Override
    public UserDTO update(UserDTO userDTO, int id) {
        userDTO.setId(id);
        User user = UserMapper.dtoToUser(userDTO);
        return repository.update(user, id);
    }

    @Override
    public UserDTO getUserById(int id) {
        return repository.getUserById(id);
    }

    @Override
    public void deleteUserById(int id) {
        repository.deleteUserById(id);
    }

    @Override
    public void deleteUsers() {
        repository.deleteUsers();
    }
}