package com.example.user.controller;

import com.example.common.result.R;
import com.example.user.dto.LoginRequest;
import com.example.user.dto.LoginResponse;
import com.example.user.dto.RegisterRequest;
import com.example.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 用户注册接口
     */
    @PostMapping("/register")
    public R register(@RequestBody RegisterRequest request) {
        try {
            userService.register(request.getPhone(), request.getPassword(), request.getNickname());
            return R.ok("注册成功", null);
        } catch (Exception e) {
            return R.error(400, e.getMessage());
        }
    }

    /**
     * 用户登录接口
     */
    @PostMapping("/login")
    public R login(@RequestBody LoginRequest request) {
        try {
            LoginResponse response = userService.login(request.getPhone(), request.getPassword());
            return R.ok("登录成功", response);
        } catch (Exception e) {
            return R.error(401, e.getMessage());
        }
    }

    /**
     * 测试接口
     */
    @GetMapping("/hello")
    public String hello() {
        return "hello user-service";
    }
}
