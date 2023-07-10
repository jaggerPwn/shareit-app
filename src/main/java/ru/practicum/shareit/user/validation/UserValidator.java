package ru.practicum.shareit.user.validation;

import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.exception.ValidationException409;
import ru.practicum.shareit.user.dto.UserDTO;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class UserValidator {

    public static void validate(UserDTO userDTO, List<UserDTO> allUsers) {
        List<String> emails = allUsers.stream().map(UserDTO::getEmail).collect(Collectors.toList());
        if (emails.contains(userDTO.getEmail())) {
            log.error("Email already in base");
            throw new ValidationException409("Email already in base");
        }
        log.debug("User validation is successful");
    }

}
