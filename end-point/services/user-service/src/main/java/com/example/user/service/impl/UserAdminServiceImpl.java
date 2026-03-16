package com.example.user.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.enums.UserRoleEnum;
import com.example.common.exception.BusinessException;
import com.example.user.entity.User;
import com.example.user.mapper.UserMapper;
import com.example.user.model.dto.UserCreateDTO;
import com.example.user.model.dto.UserUpdateDTO;
import com.example.user.model.vo.UserVO;
import com.example.user.service.UserAdminService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 管理员用户管理服务实现
 */
@Slf4j
@Service
public class UserAdminServiceImpl extends ServiceImpl<UserMapper, User> implements UserAdminService {

    @Override
    public Page<UserVO> getUserPage(Integer pageNum, Integer pageSize, String phone, Integer role) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        
        if (StrUtil.isNotBlank(phone)) {
            queryWrapper.like(User::getPhone, phone);
        }
        
        if (role != null) {
            queryWrapper.eq(User::getRole, role);
        }
        
        queryWrapper.orderByDesc(User::getCreateTime);
        
        Page<User> page = new Page<>(pageNum, pageSize);
        Page<User> result = this.page(page, queryWrapper);
        
        // 转换为 VO，不返回密码
        Page<UserVO> voPage = new Page<>(pageNum, pageSize);
        voPage.setRecords(result.getRecords().stream().map(this::convertToVO).collect(Collectors.toList()));
        voPage.setTotal(result.getTotal());
        voPage.setPages(result.getPages());
        
        return voPage;
    }

    @Override
    public UserVO getUserDetail(Long userId) {
        User user = this.getById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        return convertToVO(user);
    }

    @Override
    public void addUser(UserCreateDTO request) {
        validatePhoneAndPassword(request.getPhone(), request.getPassword());
        checkPhoneExists(request.getPhone(), null);
        
        User user = new User();
        user.setPhone(request.getPhone());
        user.setPassword(DigestUtil.md5Hex(request.getPassword()));
        user.setNickname(StrUtil.isBlank(request.getNickname()) ? "用户" + request.getPhone().substring(7) : request.getNickname());
        user.setRole(request.getRole() != null ? request.getRole() : UserRoleEnum.USER.getCode());
        
        this.save(user);
        log.info("用户已新增 | phone: {} | role: {}", request.getPhone(), request.getRole());
    }

    @Override
    public void updateUser(Long userId, UserUpdateDTO request) {
        User user = this.getById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        // 如果修改了手机号，检查是否已存在
        if (!user.getPhone().equals(request.getPhone())) {
            checkPhoneExists(request.getPhone(), userId);
        }
        
        user.setPhone(request.getPhone());
        user.setNickname(StrUtil.isBlank(request.getNickname()) ? user.getNickname() : request.getNickname());
        
        // 如果提供了角色，则更新角色
        if (request.getRole() != null) {
            user.setRole(request.getRole());
        }
        
        // 如果提供了密码，则更新密码
        if (StrUtil.isNotBlank(request.getPassword())) {
            user.setPassword(DigestUtil.md5Hex(request.getPassword()));
        }
        
        this.updateById(user);
        log.info("用户已修改 | userId: {} | phone: {}", userId, request.getPhone());
    }

    @Override
    public void deleteUser(Long userId) {
        User user = this.getById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        this.removeById(userId);
        log.info("用户已删除 | userId: {} | phone: {}", userId, user.getPhone());
    }

    @Override
    public Map<String, Object> getUserStats() {
        Long totalUsers = this.count();
        Long normalUsers = this.lambdaQuery().eq(User::getRole, UserRoleEnum.USER.getCode()).count();
        Long adminUsers = this.lambdaQuery().eq(User::getRole, UserRoleEnum.ADMIN.getCode()).count();
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", totalUsers);
        stats.put("normalUsers", normalUsers);
        stats.put("adminUsers", adminUsers);
        
        return stats;
    }

    /**
     * 验证手机号和密码
     */
    private void validatePhoneAndPassword(String phone, String password) {
        if (StrUtil.isBlank(phone)) {
            throw new BusinessException("手机号不能为空");
        }
        if (StrUtil.isBlank(password)) {
            throw new BusinessException("密码不能为空");
        }
    }

    /**
     * 检查手机号是否已存在
     */
    private void checkPhoneExists(String phone, Long excludeId) {
        User exist = this.lambdaQuery()
                .eq(User::getPhone, phone)
                .ne(excludeId != null, User::getId, excludeId)
                .one();
        if (exist != null) {
            throw new BusinessException("手机号已被注册");
        }
    }

    /**
     * 转换为 VO（不返回密码）
     */
    private UserVO convertToVO(User user) {
        UserVO vo = new UserVO();
        vo.setId(String.valueOf(user.getId()));
        vo.setPhone(user.getPhone());
        vo.setNickname(user.getNickname());
        vo.setRole(user.getRole());
        vo.setCreateTime(user.getCreateTime());
        vo.setUpdateTime(user.getUpdateTime());
        return vo;
    }
}
