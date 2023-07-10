package ru.practicum.shareit.user.dto;

import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

public class UserMapper {

    public static UserDTO userToDto(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .build();
    }

    public static User dtoToUser(UserDTO userDTO) {
        return User.builder()
                .id(userDTO.getId())
                .email(userDTO.getEmail())
                .name(userDTO.getName())
                .build();
    }

    public static List<UserDTO> userToDtoList(List<User> users) {
        List<UserDTO> collect = users.stream()
                .map(UserMapper::userToDto)
                .collect(Collectors.toList());
        return collect;
    }

    public static List<User> dtoToUserList(List<UserDTO> userDTOs) {
        List<User> collect = userDTOs.stream()
                .map(UserMapper::dtoToUser)
                .collect(Collectors.toList());
        return collect;
    }
}
