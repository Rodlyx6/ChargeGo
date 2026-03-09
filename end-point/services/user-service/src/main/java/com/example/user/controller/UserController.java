package com.example.user.controller;

import com.example.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/user")
public class UserController {
    @GetMapping("/hello")
    public String hello() {
        return "hello user";
    }

    @GetMapping("/{id}")
    public String getUser(@PathVariable Long id) {
        return "get user " + id;
    }

}
