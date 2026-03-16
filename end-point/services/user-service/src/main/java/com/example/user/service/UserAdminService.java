package com.example.user.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.user.model.dto.UserCreateDTO;
import com.example.user.model.dto.UserUpdateDTO;
import com.example.user.model.vo.UserVO;

import java.util.Map;

/**
 * 管理员用户管理服务接口
 * 职责：提供管理员端的用户增删改查、统计功能
 * 与 UserService 分离，遵循单一职责原则
 */
public interface UserAdminService {

    /**
     * 分页查询用户列表
     */
    Page<UserVO> getUserPage(Integer pageNum, Integer pageSize, String phone, Integer role);

    /**
     * 获取用户详情
     */
    UserVO getUserDetail(Long userId);

    /**
     * 新增用户
     */
    void addUser(UserCreateDTO request);

    /**
     * 修改用户
     */
    void updateUser(Long userId, UserUpdateDTO request);

    /**
     * 删除用户
     */
    void deleteUser(Long userId);

    /**
     * 获取用户统计数据
     */
    Map<String, Object> getUserStats();
}
