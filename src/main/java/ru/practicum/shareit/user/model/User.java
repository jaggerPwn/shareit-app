package ru.practicum.shareit.user.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class User {
    private int id;
    private String email;
    private String name;
}