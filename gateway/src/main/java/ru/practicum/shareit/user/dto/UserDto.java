package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.validation.CreationValidation;
import ru.practicum.shareit.validation.UpdateValidation;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private int id;
    @NotBlank(groups = {CreationValidation.class, UpdateValidation.class},
            message = "Name may not be null")
    private String name;
    @NotEmpty(groups = {CreationValidation.class},
            message = "Email can not be null")
    @Email(groups = {CreationValidation.class, UpdateValidation.class},
            regexp = ".+@.+\\..+|", message = "Email pattern is not appropriate")
    private String email;

}