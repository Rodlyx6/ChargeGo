# ChargeGo Frontend

基于 Vue 3 + Tailwind CSS 的充电桩预约平台前端项目。

## 技术栈

- Vue 3 (Composition API)
- Vue Router 4
- Pinia (状态管理)
- Tailwind CSS
- Axios
- Vite

## 项目结构

```
frontend/
├── src/
│   ├── api/           # API 接口
│   │   ├── auth.js    # 用户认证
│   │   ├── station.js # 充电桩
│   │   └── order.js   # 订单
│   ├── stores/        # Pinia 状态管理
│   │   └── auth.js    # 认证状态
│   ├── router/        # 路由配置
│   ├── views/         # 页面组件
│   │   ├── Home.vue           # 首页
│   │   ├── Login.vue          # 登录
│   │   ├── Register.vue       # 注册
│   │   ├── Stations.vue       # 充电桩搜索
│   │   ├── MyOrders.vue       # 我的订单
│   │   └── admin/             # 管理后台
│   │       ├── Dashboard.vue  # 管理概览
│   │       └── Stations.vue   # 充电桩管理
│   ├── utils/         # 工具函数
│   │   └── request.js # Axios 封装
│   ├── App.vue
│   ├── main.js
│   └── style.css
├── index.html
├── package.json
├── vite.config.js
└── tailwind.config.js
```

## 功能模块

### 用户端
- ✅ 用户注册/登录
- ✅ 附近充电桩搜索（基于地理位置）
- ✅ 充电桩预约
- ✅ 订单管理（支付、取消）

### 管理端
- ✅ 管理员登录
- ✅ 充电桩管理（增删改查）
- ✅ 数据统计概览

## 快速开始

### 安装依赖

```bash
cd frontend
npm install
```

### 启动开发服务器

```bash
npm run dev
```

访问 http://localhost:3000

### 构建生产版本

```bash
npm run build
```

## API 配置

后端 API 地址配置在 `vite.config.js` 中：

```javascript
server: {
  proxy: {
    '/api': {
      target: 'http://localhost:8000',  // 网关地址
      changeOrigin: true,
      rewrite: (path) => path.replace(/^\/api/, '')
    }
  }
}
```

## 测试账号

- 管理员: 13800000000 / 123456
- 普通用户: 需要注册

## 设计特点

- 🎨 现代化 UI 设计，参考 Walmart 风格
- 🎯 响应式布局，支持移动端
- ⚡ 流畅的动画效果
- 🔐 完整的权限控制
- 📱 地理位置服务集成
- 🎭 优雅的错误处理

## 注意事项

1. 后端需要先启动网关服务（端口 8000）
2. 使用地理位置功能需要 HTTPS 或 localhost
3. 订单列表功能需要后端补充 API 接口
