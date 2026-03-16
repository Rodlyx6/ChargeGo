package com.example.user.controller;

import com.example.common.enums.UserRoleEnum;
import com.example.common.exception.BusinessException;
import com.example.common.result.R;
import com.example.user.model.dto.UserLoginDTO;
import com.example.user.model.dto.UserRegisterDTO;
import com.example.user.model.vo.UserLoginVO;
import com.example.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 用户控制器
 * 职责：处理用户端的注册、登陆等接口
 * 权限：无需认证
 */
@Slf4j
@RestController
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 用户注册
     */
    @PostMapping("/user/register")
    public R register(@RequestBody UserRegisterDTO request) {
        log.info("📝 用户注册 | phone: {}", request.getPhone());
        userService.register(request.getPhone(), request.getPassword(), request.getNickname());
        return R.ok("注册成功", null);
    }

    /**
     * 用户登陆
     */
    @PostMapping("/user/login")
    public R userLogin(@RequestBody UserLoginDTO request) {
        log.info("🔐 用户登陆 | phone: {}", request.getPhone());
        UserLoginVO response = userService.login(request.getPhone(), request.getPassword());
        
        // 管理员不能用此接口
        if (UserRoleEnum.isAdmin(response.getRole())) {
            throw new BusinessException(403, "管理员请使用管理员登陆接口");
        }
        
        return R.ok("登陆成功", response);
    }

    /**
     * 管理员登陆
     */
    @PostMapping("/admin/login")
    public R adminLogin(@RequestBody UserLoginDTO request) {
        log.info("🔐 管理员登陆 | phone: {}", request.getPhone());
        UserLoginVO response = userService.login(request.getPhone(), request.getPassword());
        
        // 只有管理员才能登陆
        if (!UserRoleEnum.isAdmin(response.getRole())) {
            throw new BusinessException(403, "您没有管理员权限");
        }
        
        return R.ok("登陆成功", response);
    }
}
