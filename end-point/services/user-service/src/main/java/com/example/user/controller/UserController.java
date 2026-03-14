package com.example.user.controller;

import com.example.common.constant.UserRole;
import com.example.common.exception.BusinessException;
import com.example.common.result.R;
import com.example.user.dto.LoginRequest;
import com.example.user.dto.LoginResponse;
import com.example.user.dto.RegisterRequest;
import com.example.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 用户注册
     */
    @PostMapping("/user/register")
    public R register(@RequestBody RegisterRequest request) {
        userService.register(request.getPhone(), request.getPassword(), request.getNickname());
        return R.ok("注册成功", null);
    }

    /**
     * 用户登陆
     */
    @PostMapping("/user/login")
    public R userLogin(@RequestBody LoginRequest request) {
        LoginResponse response = userService.login(request.getPhone(), request.getPassword());
        
        // 管理员不能用此接口
        if (UserRole.ADMIN.equals(response.getRole())) {
            throw new BusinessException(403, "管理员请使用管理员登陆接口");
        }
        
        return R.ok("登陆成功", response);
    }

    /**
     * 管理员登陆
     */
    @PostMapping("/admin/login")
    public R adminLogin(@RequestBody LoginRequest request) {
        LoginResponse response = userService.login(request.getPhone(), request.getPassword());
        
        // 只有管理员才能登陆
        if (!UserRole.ADMIN.equals(response.getRole())) {
            throw new BusinessException(403, "您没有管理员权限");
        }
        
        return R.ok("登陆成功", response);
    }

    /**
     * 测试接口
     */
    @GetMapping("/user/hello")
    public String hello() {
        return "hello user-service";
    }
}
