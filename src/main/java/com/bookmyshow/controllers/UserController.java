package com.bookmyshow.controllers;

import com.bookmyshow.models.User;
import com.bookmyshow.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping
    public User createUser(@RequestBody User user) {
        return userService.createUser(user);
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }
    
    @GetMapping("/{id}/{password}")
    public User getUserById(@PathVariable Long id, @PathVariable String password) {
        return userService.getUserByIdAndPassword(id, password);
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }
}
