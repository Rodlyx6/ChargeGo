package com.example.user.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.common.result.R;
import com.example.user.model.dto.UserCreateDTO;
import com.example.user.model.dto.UserUpdateDTO;
import com.example.user.model.vo.UserVO;
import com.example.user.service.UserAdminService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 用户管理控制器（管理员端）
 * 职责：处理管理员端的用户增删改查、统计等接口
 * 权限：仅管理员可访问（通过网关权限验证）
 */
@Slf4j
@RestController
@RequestMapping("/admin/user")
public class AdminUserController {

    @Autowired
    private UserAdminService userAdminService;

    /**
     * 分页查询用户列表
     */
    @GetMapping("/list")
    public R getUserList(@RequestHeader("X-User-Id") Long adminId,
                         @RequestParam(defaultValue = "1") Integer pageNum,
                         @RequestParam(defaultValue = "10") Integer pageSize,
                         @RequestParam(required = false) String phone,
                         @RequestParam(required = false) Integer role) {
        log.info("📋 查询用户列表 | adminId: {} | pageNum: {} | pageSize: {} | phone: {} | role: {}", 
                adminId, pageNum, pageSize, phone, role);

        Page<UserVO> result = userAdminService.getUserPage(pageNum, pageSize, phone, role);
        return R.ok("查询成功", result);
    }

    /**
     * 查询用户详情
     */
    @GetMapping("/{userId}")
    public R getUserDetail(@RequestHeader("X-User-Id") Long adminId,
                           @PathVariable Long userId) {
        log.info("🔍 查询用户详情 | adminId: {} | userId: {}", adminId, userId);

        UserVO user = userAdminService.getUserDetail(userId);
        return R.ok("查询成功", user);
    }

    /**
     * 新增用户
     */
    @PostMapping("/add")
    public R addUser(@RequestHeader("X-User-Id") Long adminId,
                     @RequestBody UserCreateDTO request) {
        log.info("➕ 新增用户 | adminId: {} | phone: {}", adminId, request.getPhone());

        userAdminService.addUser(request);
        return R.ok("新增成功", null);
    }

    /**
     * 修改用户
     */
    @PutMapping("/{userId}")
    public R updateUser(@RequestHeader("X-User-Id") Long adminId,
                        @PathVariable Long userId,
                        @RequestBody UserUpdateDTO request) {
        log.info("✏️ 修改用户 | adminId: {} | userId: {}", adminId, userId);

        userAdminService.updateUser(userId, request);
        return R.ok("修改成功", null);
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/{userId}")
    public R deleteUser(@RequestHeader("X-User-Id") Long adminId,
                        @PathVariable Long userId) {
        log.info("🗑️ 删除用户 | adminId: {} | userId: {}", adminId, userId);

        userAdminService.deleteUser(userId);
        return R.ok("删除成功", null);
    }

    /**
     * 获取用户统计数据
     */
    @GetMapping("/stats")
    public R getUserStats(@RequestHeader("X-User-Id") Long adminId) {
        log.info("📊 查询用户统计 | adminId: {}", adminId);

        Map<String, Object> stats = userAdminService.getUserStats();
        return R.ok("查询成功", stats);
    }
}
