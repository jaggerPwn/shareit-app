package ru.practicum.shareit.user.dto;

import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

public class UserMapper {

    public static UserDto userToDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .build();
    }

    public static User dtoToUser(UserDto userDTO) {
        return User.builder()
                .id(userDTO.getId())
                .email(userDTO.getEmail())
                .name(userDTO.getName())
                .build();
    }

    public static List<UserDto> userToDtoList(List<User> users) {
        return users.stream()
                .map(UserMapper::userToDto)
                .collect(Collectors.toList());
    }

    public static List<User> dtoToUserList(List<UserDto> userDtos) {
        return userDtos.stream()
                .map(UserMapper::dtoToUser)
                .collect(Collectors.toList());
    }
}
