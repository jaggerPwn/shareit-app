package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@Data
@Builder

public class UserDTO {
    private int id;
    @NotEmpty(message = "Name may not be null")
    @Email(regexp = ".+@.+\\..+|")
    private String email;
    private String name;
}