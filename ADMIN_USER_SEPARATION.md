# 管理员与普通用户权限分离方案

## 🎯 需求
管理员登录后应该：
- ✅ 直接跳转到管理后台
- ✅ 只显示管理相关的菜单（管理概览、充电桩管理）
- ✅ 不显示普通用户的菜单（首页、找充电桩、我的订单）
- ✅ 无法访问普通用户的页面

普通用户登录后应该：
- ✅ 跳转到首页
- ✅ 只显示普通用户的菜单
- ✅ 无法访问管理后台

---

## 🔧 实施方案

### 1. 导航菜单分离（Home.vue）

**修改前**：
```vue
<nav v-if="isAuthenticated">
  <router-link to="/">首页</router-link>
  <router-link to="/stations">找充电桩</router-link>
  <router-link to="/my-orders">我的订单</router-link>
  <router-link v-if="isAdmin" to="/admin">管理后台</router-link>
</nav>
```

**修改后**：
```vue
<!-- 普通用户导航 -->
<nav v-if="isAuthenticated && !isAdmin">
  <router-link to="/">首页</router-link>
  <router-link to="/stations">找充电桩</router-link>
  <router-link to="/my-orders">我的订单</router-link>
</nav>

<!-- 管理员导航 -->
<nav v-if="isAuthenticated && isAdmin">
  <router-link to="/admin">管理概览</router-link>
  <router-link to="/admin/stations">充电桩管理</router-link>
</nav>
```

---

### 2. 路由守卫优化（router/index.js）

**新增逻辑**：

#### 2.1 登录后重定向
```javascript
// 如果已登录且访问登录/注册页
if ((to.name === 'Login' || to.name === 'Register') && authStore.isAuthenticated) {
  if (authStore.isAdmin) {
    next({ name: 'Admin' })  // 管理员 → 管理后台
  } else {
    next({ name: 'Home' })   // 普通用户 → 首页
  }
  return
}
```

#### 2.2 管理员访问限制
```javascript
// 管理员访问普通用户页面，重定向到管理后台
if (authStore.isAdmin && (to.name === 'Stations' || to.name === 'MyOrders')) {
  next({ name: 'Admin' })
  return
}
```

#### 2.3 首页访问控制
```javascript
// 管理员访问首页，自动跳转到管理后台
if (to.name === 'Home' && authStore.isAuthenticated && authStore.isAdmin) {
  next({ name: 'Admin' })
  return
}
```

---

### 3. 登录逻辑优化（Login.vue）

**确保跳转正确**：
```javascript
const handleLogin = async () => {
  if (isAdminLogin.value) {
    await authStore.adminLogin(phone, password)
    router.push('/admin')  // 管理员 → 管理后台
  } else {
    await authStore.login(phone, password)
    const redirect = route.query.redirect || '/'
    router.push(redirect)  // 普通用户 → 重定向页面或首页
  }
}
```

---

## 📊 权限控制矩阵

| 页面 | 普通用户 | 管理员 | 未登录 |
|------|---------|--------|--------|
| 首页 (/) | ✅ 可访问 | ❌ 自动跳转到 /admin | ✅ 可访问 |
| 登录 (/login) | ❌ 自动跳转到 / | ❌ 自动跳转到 /admin | ✅ 可访问 |
| 注册 (/register) | ❌ 自动跳转到 / | ❌ 自动跳转到 /admin | ✅ 可访问 |
| 找充电桩 (/stations) | ✅ 可访问 | ❌ 自动跳转到 /admin | ❌ 跳转到登录 |
| 我的订单 (/my-orders) | ✅ 可访问 | ❌ 自动跳转到 /admin | ❌ 跳转到登录 |
| 管理概览 (/admin) | ❌ 跳转到首页 | ✅ 可访问 | ❌ 跳转到登录 |
| 充电桩管理 (/admin/stations) | ❌ 跳转到首页 | ✅ 可访问 | ❌ 跳转到登录 |

---

## 🎯 用户体验流程

### 普通用户流程
```
1. 访问首页 → 点击"登录"
2. 输入账号密码 → 点击"登录"
3. 自动跳转到首页
4. 看到导航：首页 | 找充电桩 | 我的订单
5. 可以正常使用所有普通功能
```

### 管理员流程
```
1. 访问首页 → 点击"登录"
2. 勾选"管理员登录" → 输入账号密码
3. 自动跳转到管理后台 (/admin)
4. 看到导航：管理概览 | 充电桩管理
5. 只能访问管理相关页面
6. 如果尝试访问 /stations 或 /my-orders，自动跳转回 /admin
```

---

## 🔒 安全性保障

### 前端层面
1. ✅ 路由守卫拦截未授权访问
2. ✅ 导航菜单根据角色动态显示
3. ✅ 自动重定向防止误操作

### 后端层面
1. ✅ 网关统一鉴权（AuthFilter）
2. ✅ JWT Token 包含角色信息
3. ✅ 管理员接口路径检查（/admin/*）

---

## 🧪 测试用例

### 测试1：普通用户登录
1. 使用普通用户账号登录
2. 验证跳转到首页
3. 验证导航菜单只显示：首页、找充电桩、我的订单
4. 尝试访问 /admin，验证被拦截

### 测试2：管理员登录
1. 勾选"管理员登录"
2. 使用管理员账号登录（13800000000 / 123456）
3. 验证跳转到 /admin
4. 验证导航菜单只显示：管理概览、充电桩管理
5. 尝试访问 /stations，验证自动跳转回 /admin

### 测试3：角色切换
1. 管理员登录后退出
2. 使用普通用户登录
3. 验证界面完全切换为普通用户视图

---

## 📝 注意事项

1. **退出登录后清除状态**
   - 确保 `authStore.logout()` 清除所有用户信息
   - 跳转到首页或登录页

2. **刷新页面保持状态**
   - Token 和用户信息存储在 localStorage
   - 页面刷新后自动恢复登录状态

3. **管理员误操作保护**
   - 管理员无法通过 URL 直接访问普通用户页面
   - 所有访问都会被路由守卫拦截并重定向

---

## ✅ 修改文件清单

1. ✅ `frontend/src/views/Home.vue` - 导航菜单分离
2. ✅ `frontend/src/router/index.js` - 路由守卫优化
3. ✅ `frontend/src/views/Login.vue` - 登录跳转逻辑

---

**现在管理员和普通用户的权限已经完全分离！** 🎉
