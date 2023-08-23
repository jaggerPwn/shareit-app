package ru.practicum.shareit.user.validation;

import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.exception.ValidationException404;
import ru.practicum.shareit.exception.ValidationException409;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Optional;

@Slf4j
public class UserValidator {

    public static void validateForDuplicateMail(UserDto userDTO, UserRepository userRepository) {
        Optional<User> existingUserFromRepository = userRepository.findById(userDTO.getId());
        if (userRepository.existsUserByEmail(userDTO.getEmail())) {
            if (existingUserFromRepository.isEmpty()) throw new ValidationException409("Email already in base");
            else if (existingUserFromRepository.get().getId() != userDTO.getId()) {
                log.error("Email already in base");
                throw new ValidationException409("Email already in base");
            }
        }
        log.debug("User validation is successful");
    }

    public static void validateIfUserExists(int userId, UserRepository userRepository) {
        if (!userRepository.existsById(userId))
            throw new ValidationException404("User " + userId + " doesn't exists");
    }
}
