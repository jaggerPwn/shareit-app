package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.validation.CreationValidation;
import ru.practicum.shareit.validation.UpdateValidation;

import javax.validation.Valid;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserClient userClient;

    @GetMapping
    public ResponseEntity<Object> getAllUsers() {
        return userClient.findUsers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getUserById(@PathVariable int id) {
        return userClient.findUser(id);
    }

    @Validated(CreationValidation.class)
    @PostMapping
    public ResponseEntity<Object> saveNewUser(@Valid @RequestBody UserDto userDTO) {
        return userClient.saveUser(userDTO);
    }

    @Validated(UpdateValidation.class)
    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateUser(@Valid @RequestBody UserDto userDTO, @PathVariable int id) {
        return userClient.updateUser(userDTO, id);
    }

    @DeleteMapping("/{id}")
    public void deleteUserById(@PathVariable int id) {
        userClient.deleteUser(id);
    }

    @DeleteMapping
    private void deleteUsers() {
        userClient.deleteAll();
    }
}
