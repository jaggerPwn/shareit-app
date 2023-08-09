package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @GetMapping
    public List<UserDto> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public UserDto getUserById(@PathVariable int id) {
        return userService.getUserById(id);
    }

    @PostMapping
    public UserDto saveNewUser(@Valid @RequestBody UserDto userDTO) {
        return userService.saveUser(userDTO);
    }

    @PatchMapping("/{id}")
    public UserDto put(@RequestBody UserDto userDTO, @PathVariable int id) {
        userDTO.setId(id);
        return userService.saveUser(userDTO);
    }

    @DeleteMapping("/{id}")
    public void deleteUserById(@PathVariable int id) {
        userService.deleteUserById(id);
    }

    @DeleteMapping
    private void deleteUsers(){
        userService.deleteUsers();
    }
}
