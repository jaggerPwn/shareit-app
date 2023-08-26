package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ValidationException404;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    @Autowired
    private final UserRepository repository;

    @Override
    public List<UserDto> getAllUsers() {
        List<User> users = repository.findAll();
        return UserMapper.userToDtoList(users);
    }

    @Override
    public UserDto saveUser(UserDto userDTO) {
        Optional<User> userOptional = repository.findById(userDTO.getId());
        if (userOptional.isPresent()) {
            User userFromDb = userOptional.get();
            if (userDTO.getEmail() == null) {
                userDTO.setEmail(userFromDb.getEmail());
            }
            if (userDTO.getName() == null) {
                userDTO.setName(userFromDb.getName());
            }
        }
        return UserMapper.userToDto(repository.save(UserMapper.dtoToUser(userDTO)));
    }

    @Override
    public UserDto getUserById(int id) {
        return UserMapper.userToDto(repository.findById(id)
                .orElseThrow(() -> new ValidationException404("user " + id + "not found")));
    }

    @Override
    public void deleteUserById(int id) {
        repository.deleteById(id);
    }

    @Override
    @Transactional
    public void deleteUsers() {
        repository.deleteAll();
        repository.setUserIdToOne();

    }
}