# ChargeGo 后端代码重构方案

## 📋 现状分析

### 当前存在的问题

#### 1. DTO 命名不规范
```
❌ 当前命名：
- LoginRequest（请求）
- LoginResponse（响应）
- CreateOrderRequest（请求）
- PaymentRequest（请求）
- NearbyStationVO（视图对象）
- OrderDetailVO（视图对象）
- StationRequest（请求，但用于新增和修改）

问题：
- Request 和 VO 混用，语义不清
- 缺少统一的命名规范
- 没有区分 DTO（数据传输）和 VO（视图对象）
```

#### 2. 包结构不够清晰
```
❌ 当前结构：
dto/
  ├── LoginRequest.java
  ├── LoginResponse.java
  ├── CreateOrderRequest.java
  └── OrderDetailVO.java

问题：
- 请求、响应、视图对象混在一起
- 无法一眼看出是输入还是输出
- 不利于维护和扩展
```

#### 3. 配置类重复
```
❌ 每个服务都有：
- MyBatisPlusConfig.java（3个服务重复）
- RedisConfig.java（3个服务重复）

问题：
- 代码重复，维护成本高
- 应该抽取到 common 模块
```

#### 4. Service 拆分过细
```
❌ order-service 有4个 Service：
- OrderCreateService
- OrderPaymentService
- OrderCancelService
- OrderQueryService

问题：
- 按功能拆分过细，导致类过多
- 应该合并为 OrderService
- Controller 也有4个，应该合并为1个
```

---

## 📚 企业级标准参考

### 阿里巴巴 Java 开发手册

#### DTO 命名规范
```
xxxDTO    - 数据传输对象（服务间传输）
xxxVO     - 视图对象（返回给前端）
xxxQuery  - 查询条件封装
xxxBO     - 业务对象（Service 层内部使用）
```

#### 包结构规范
```
com.example.xxx
├── controller      // 控制器层
├── service         // 服务层
│   └── impl
├── mapper          // 数据访问层
├── entity          // 实体类（对应数据库表）
├── model           // 模型层
│   ├── dto         // 数据传输对象
│   ├── vo          // 视图对象
│   └── query       // 查询对象
├── converter       // 对象转换器
├── enums           // 枚举类
├── config          // 配置类
└── exception       // 异常类
```

---

## 🎯 重构目标

### 1. 统一命名规范
- **请求 DTO**：`XxxDTO`（如 `UserLoginDTO`）
- **响应 VO**：`XxxVO`（如 `UserLoginVO`）
- **查询对象**：`XxxQuery`（如 `OrderQuery`）

### 2. 优化包结构
```
com.example.xxx
├── controller
├── service
│   └── impl
├── mapper
├── entity
├── model
│   ├── dto
│   ├── vo
│   └── query
├── converter
├── config
└── enums
```

### 3. 抽取公共配置
- MyBatisPlusConfig → common 模块
- RedisConfig → common 模块

### 4. 合并 Service
- 4个 OrderService → 1个 OrderService
- 4个 OrderController → 1个 OrderController

---

## 📐 详细重构方案

### 第一阶段：user-service 重构

#### 重构前
```
com.example.user
├── dto
│   ├── LoginRequest.java
│   ├── LoginResponse.java
│   └── RegisterRequest.java
```

#### 重构后
```
com.example.user
├── model
│   ├── dto
│   │   ├── UserLoginDTO.java      // 登录请求
│   │   └── UserRegisterDTO.java   // 注册请求
│   └── vo
│       └── UserLoginVO.java       // 登录响应
```

#### 命名对照表
| 旧名称 | 新名称 | 说明 |
|--------|--------|------|
| LoginRequest | UserLoginDTO | 登录请求 |
| RegisterRequest | UserRegisterDTO | 注册请求 |
| LoginResponse | UserLoginVO | 登录响应 |

---

### 第二阶段：station-service 重构

#### 重构前
```
com.example.station
├── dto
│   ├── StationRequest.java
│   ├── NearbySearchRequest.java
│   └── NearbyStationVO.java
```

#### 重构后
```
com.example.station
├── model
│   ├── dto
│   │   ├── StationCreateDTO.java     // 新增充电桩
│   │   └── StationUpdateDTO.java     // 修改充电桩
│   ├── vo
│   │   ├── StationVO.java            // 充电桩详情
│   │   └── StationListVO.java        // 充电桩列表
│   └── query
│       └── NearbyStationQuery.java   // 附近充电桩查询
```

#### 命名对照表
| 旧名称 | 新名称 | 说明 |
|--------|--------|------|
| StationRequest | StationCreateDTO / StationUpdateDTO | 拆分为新增和修改 |
| NearbySearchRequest | NearbyStationQuery | 查询条件 |
| NearbyStationVO | StationListVO | 列表视图 |

---

### 第三阶段：order-service 重构（重点）

#### 重构前
```
com.example.order
├── dto
│   ├── CreateOrderRequest.java
│   ├── PaymentRequest.java
│   └── OrderDetailVO.java
├── controller
│   ├── OrderCreateController.java
│   ├── OrderPaymentController.java
│   ├── OrderCancelController.java
│   └── OrderQueryController.java
├── service
│   ├── OrderCreateService.java
│   ├── OrderPaymentService.java
│   ├── OrderCancelService.java
│   └── OrderQueryService.java
```

#### 重构后
```
com.example.order
├── model
│   ├── dto
│   │   ├── OrderCreateDTO.java       // 创建订单
│   │   ├── OrderPayDTO.java          // 支付订单
│   │   └── OrderCancelDTO.java       // 取消订单
│   ├── vo
│   │   ├── OrderVO.java              // 订单详情
│   │   └── OrderListVO.java          // 订单列表
│   └── query
│       └── OrderQuery.java           // 订单查询
├── controller
│   └── OrderController.java          // 统一的订单控制器
├── service
│   ├── OrderService.java             // 统一的订单服务
│   └── impl
│       └── OrderServiceImpl.java
```

#### Service 合并
```java
// 旧：4个 Service
OrderCreateService    → createOrder()
OrderPaymentService   → payOrder()
OrderCancelService    → cancelOrder()
OrderQueryService     → getOrderList(), getOrderDetail()

// 新：1个 Service
OrderService {
    createOrder()      // 创建订单
    payOrder()         // 支付订单
    cancelOrder()      // 取消订单
    getOrderList()     // 查询订单列表
    getOrderDetail()   // 查询订单详情
}
```

---

### 第四阶段：抽取公共配置

#### 1. MyBatisPlusConfig → common

**创建**：`common/src/main/java/com/example/common/config/MyBatisPlusConfig.java`

**删除**：
- user-service/config/MyBatisPlusConfig.java
- station-service/config/MyBatisPlusConfig.java
- order-service/config/MyBatisPlusConfig.java

#### 2. RedisConfig → common

**创建**：`common/src/main/java/com/example/common/config/RedisConfig.java`

**删除**：
- user-service/config/RedisConfig.java
- station-service/config/RedisConfig.java
- order-service/config/RedisConfig.java

---

### 第五阶段：优化枚举类

#### 重构前
```java
// OrderStatus.java
public class OrderStatus {
    public static final int UNPAID = 0;
    public static final int PAID = 1;
    public static final int COMPLETED = 2;
    public static final int CANCELLED = 3;
}
```

#### 重构后
```java
// OrderStatusEnum.java
@Getter
public enum OrderStatusEnum {
    UNPAID(0, "待支付"),
    PAID(1, "充电中"),
    COMPLETED(2, "已完成"),
    CANCELLED(3, "已取消");
    
    private final Integer code;
    private final String desc;
    
    OrderStatusEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
```

---

## 📊 重构前后对比

### 包结构对比

#### order-service 重构前
```
com.example.order
├── config (4个配置类)
├── controller (4个控制器)
├── dto (3个DTO)
├── service (4个Service + 4个实现类)
```

#### order-service 重构后
```
com.example.order
├── config (2个配置类)
├── controller (1个控制器)
├── model
│   ├── dto (3个DTO)
│   ├── vo (2个VO)
│   └── query (1个Query)
├── service (1个Service + 1个实现类)
├── converter (1个转换器)
```

### 代码量对比

| 项目 | 重构前 | 重构后 | 减少 |
|------|--------|--------|------|
| Service 类数量 | 8个 | 2个 | -75% |
| Controller 类数量 | 4个 | 1个 | -75% |
| 配置类数量 | 12个 | 6个 | -50% |

---

## ✅ 重构收益

### 1. 可读性提升
- ✅ 命名统一，一眼看出是请求、响应还是视图对象
- ✅ 包结构清晰，快速定位代码
- ✅ 职责明确，每个类只做一件事

### 2. 可维护性提升
- ✅ 减少重复代码
- ✅ Service 合并，业务逻辑集中管理
- ✅ 公共配置统一维护

### 3. 可扩展性提升
- ✅ 新增功能按统一规范添加
- ✅ 使用 Converter 解耦对象转换
- ✅ 使用枚举替代常量

---

## 🚀 实施步骤

### 阶段一：准备工作（0.5天）
1. 备份当前代码
2. 创建新分支 `refactor/code-structure`

### 阶段二：重构 common 模块（1天）
1. 抽取公共配置
2. 优化枚举类

### 阶段三：重构 user-service（1天）
1. 创建 model 包结构
2. 重命名 DTO

### 阶段四：重构 station-service（1天）
1. 创建 model 包结构
2. 拆分 StationRequest

### 阶段五：重构 order-service（2天）
1. 创建 model 包结构
2. 合并 Service 和 Controller

### 阶段六：测试和优化（1.5天）
1. 全面测试
2. 更新文档

**总计：7个工作日**

---

**这是一个严谨的企业级代码重构方案，参考了阿里巴巴Java开发手册和主流开源项目的最佳实践。**

**是否开始实施重构？我可以逐步帮你完成每个阶段的代码重构。**
