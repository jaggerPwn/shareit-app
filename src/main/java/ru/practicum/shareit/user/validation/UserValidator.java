package ru.practicum.shareit.user.validation;

import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.exception.ValidationException404;
import ru.practicum.shareit.exception.ValidationException409;
import ru.practicum.shareit.user.dto.UserDTO;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class UserValidator {

    public static void validateForDuplicateMail(UserDTO userDTO, List<UserDTO> allUsers) {
        List<String> emails = allUsers.stream().map(UserDTO::getEmail).collect(Collectors.toList());
        if (emails.contains(userDTO.getEmail())) {
            log.error("Email already in base");
            throw new ValidationException409("Email already in base");
        }
        log.debug("User validation is successful");
    }

    public static void validateIfUserExists(int userId, UserService userService) {
        List<Integer> userList = userService.getAllUsers().stream()
                .map(UserDTO::getId)
                .collect(Collectors.toList());
        if (!userList.contains(userId))
            throw new ValidationException404("User " + userId + " doesn't exists");
    }
}
