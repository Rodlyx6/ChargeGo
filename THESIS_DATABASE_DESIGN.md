# ChargeGo 共享充电桩平台 - 数据库设计文档

## 1. 数据库概述

### 1.1 数据库选择
- **数据库系统**：MySQL 8.0+
- **字符集**：utf8mb4（支持中文和表情符号）
- **排序规则**：utf8mb4_unicode_ci

### 1.2 数据库架构
```
ChargeGo 项目
├── charging_user（用户数据库）
├── charging_station（充电桩数据库）
└── charging_order（订单数据库）
```

---

## 2. 用户数据库（charging_user）

### 2.1 用户表（user）

#### 表结构
```sql
CREATE TABLE user (
  id BIGINT PRIMARY KEY COMMENT '用户ID（雪花算法生成）',
  phone VARCHAR(20) UNIQUE NOT NULL COMMENT '手机号',
  password VARCHAR(255) NOT NULL COMMENT '密码（BCrypt加密）',
  nickname VARCHAR(50) NOT NULL COMMENT '昵称',
  role INT DEFAULT 0 COMMENT '角色（0普通用户，1管理员）',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

#### 字段说明
| 字段名 | 类型 | 长度 | 约束 | 说明 |
|--------|------|------|------|------|
| id | BIGINT | - | PRIMARY KEY | 用户ID（雪花算法生成） |
| phone | VARCHAR | 20 | UNIQUE NOT NULL | 手机号 |
| password | VARCHAR | 255 | NOT NULL | 密码（BCrypt加密） |
| nickname | VARCHAR | 50 | NOT NULL | 昵称 |
| role | INT | - | DEFAULT 0 | 角色（0普通用户，1管理员） |
| create_time | DATETIME | - | DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| update_time | DATETIME | - | DEFAULT CURRENT_TIMESTAMP | 更新时间 |

#### 索引设计
```sql
-- 手机号唯一索引
CREATE UNIQUE INDEX idx_phone ON user(phone);

-- 角色索引
CREATE INDEX idx_role ON user(role);

-- 创建时间索引
CREATE INDEX idx_create_time ON user(create_time);
```

#### 数据示例
```
id: 2031229876161310722
phone: 13800000000
password: $2a$10$... (BCrypt加密)
nickname: 超级管理员
role: 1
create_time: 2026-03-16 10:00:00
update_time: 2026-03-16 10:00:00
```

---

## 3. 充电桩数据库（charging_station）

### 3.1 充电桩表（station）

#### 表结构
```sql
CREATE TABLE station (
  id BIGINT PRIMARY KEY COMMENT '充电桩ID（雪花算法生成）',
  sn_code VARCHAR(50) UNIQUE NOT NULL COMMENT '充电桩编号',
  address VARCHAR(255) NOT NULL COMMENT '地址',
  longitude DECIMAL(10, 6) NOT NULL COMMENT '经度',
  latitude DECIMAL(10, 6) NOT NULL COMMENT '纬度',
  status INT DEFAULT 0 COMMENT '状态（0空闲，1预约中，2充电中，3故障）',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

#### 字段说明
| 字段名 | 类型 | 长度 | 约束 | 说明 |
|--------|------|------|------|------|
| id | BIGINT | - | PRIMARY KEY | 充电桩ID（雪花算法生成） |
| sn_code | VARCHAR | 50 | UNIQUE NOT NULL | 充电桩编号 |
| address | VARCHAR | 255 | NOT NULL | 地址 |
| longitude | DECIMAL | 10,6 | NOT NULL | 经度 |
| latitude | DECIMAL | 10,6 | NOT NULL | 纬度 |
| status | INT | - | DEFAULT 0 | 状态（0空闲，1预约中，2充电中，3故障） |
| create_time | DATETIME | - | DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| update_time | DATETIME | - | DEFAULT CURRENT_TIMESTAMP | 更新时间 |

#### 索引设计
```sql
-- 编号唯一索引
CREATE UNIQUE INDEX idx_sn_code ON station(sn_code);

-- 状态索引
CREATE INDEX idx_status ON station(status);

-- 地理位置索引（用于附近搜索）
CREATE SPATIAL INDEX idx_location ON station(POINT(longitude, latitude));

-- 创建时间索引
CREATE INDEX idx_create_time ON station(create_time);
```

#### 数据示例
```
id: 101
sn_code: SN-A001
address: 科技园A栋地下车库
longitude: 113.943100
latitude: 22.541100
status: 0
create_time: 2026-03-16 10:00:00
update_time: 2026-03-16 10:00:00
```

---

## 4. 订单数据库（charging_order）

### 4.1 订单表（order_info）

#### 表结构
```sql
CREATE TABLE order_info (
  id BIGINT PRIMARY KEY COMMENT '订单ID（雪花算法生成）',
  order_no VARCHAR(50) UNIQUE NOT NULL COMMENT '订单号',
  user_id BIGINT NOT NULL COMMENT '用户ID',
  station_id BIGINT NOT NULL COMMENT '充电桩ID',
  status INT DEFAULT 0 COMMENT '订单状态（0待支付，1充电中，2已完成，3已取消）',
  charge_type INT NOT NULL COMMENT '充电类型（1快充，2普通充电，3慢充）',
  charge_time INT NOT NULL COMMENT '预约充电时间（小时）',
  expected_amount DECIMAL(10, 2) COMMENT '预期金额',
  actual_amount DECIMAL(10, 2) COMMENT '实际金额',
  refund_amount DECIMAL(10, 2) COMMENT '退款金额',
  actual_charge_time DECIMAL(10, 2) COMMENT '实际充电时间（小时）',
  pay_time DATETIME COMMENT '支付时间',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

#### 字段说明
| 字段名 | 类型 | 长度 | 约束 | 说明 |
|--------|------|------|------|------|
| id | BIGINT | - | PRIMARY KEY | 订单ID（雪花算法生成） |
| order_no | VARCHAR | 50 | UNIQUE NOT NULL | 订单号 |
| user_id | BIGINT | - | NOT NULL | 用户ID |
| station_id | BIGINT | - | NOT NULL | 充电桩ID |
| status | INT | - | DEFAULT 0 | 订单状态（0待支付，1充电中，2已完成，3已取消） |
| charge_type | INT | - | NOT NULL | 充电类型（1快充，2普通充电，3慢充） |
| charge_time | INT | - | NOT NULL | 预约充电时间（小时） |
| expected_amount | DECIMAL | 10,2 | - | 预期金额 |
| actual_amount | DECIMAL | 10,2 | - | 实际金额 |
| refund_amount | DECIMAL | 10,2 | - | 退款金额 |
| actual_charge_time | DECIMAL | 10,2 | - | 实际充电时间（小时） |
| pay_time | DATETIME | - | - | 支付时间 |
| create_time | DATETIME | - | DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| update_time | DATETIME | - | DEFAULT CURRENT_TIMESTAMP | 更新时间 |

#### 索引设计
```sql
-- 订单号唯一索引
CREATE UNIQUE INDEX idx_order_no ON order_info(order_no);

-- 用户ID索引
CREATE INDEX idx_user_id ON order_info(user_id);

-- 充电桩ID索引
CREATE INDEX idx_station_id ON order_info(station_id);

-- 订单状态索引
CREATE INDEX idx_status ON order_info(status);

-- 创建时间索引
CREATE INDEX idx_create_time ON order_info(create_time);

-- 支付时间索引
CREATE INDEX idx_pay_time ON order_info(pay_time);

-- 用户ID + 状态复合索引
CREATE INDEX idx_user_status ON order_info(user_id, status);

-- 充电桩ID + 状态复合索引
CREATE INDEX idx_station_status ON order_info(station_id, status);
```

#### 数据示例
```
id: 1773667959433C77C
order_no: 1773667959433C77C
user_id: 2031229876161310722
station_id: 101
status: 2
charge_type: 1
charge_time: 1
expected_amount: 10.00
actual_amount: 0.20
refund_amount: 9.80
actual_charge_time: 0.02
pay_time: 2026-03-16 21:33:00
create_time: 2026-03-16 21:32:39
update_time: 2026-03-16 21:35:00
```

---

## 5. 数据库关系图

```
┌─────────────────────┐
│      user           │
├─────────────────────┤
│ id (PK)             │
│ phone (UNIQUE)      │
│ password            │
│ nickname            │
│ role                │
│ create_time         │
│ update_time         │
└──────────┬──────────┘
           │
           │ 1:N
           │
           ▼
┌─────────────────────┐
│    order_info       │
├─────────────────────┤
│ id (PK)             │
│ order_no (UNIQUE)   │
│ user_id (FK)        │◄─────┐
│ station_id (FK)     │      │
│ status              │      │
│ charge_type         │      │
│ charge_time         │      │
│ expected_amount     │      │
│ actual_amount       │      │
│ refund_amount       │      │
│ actual_charge_time  │      │
│ pay_time            │      │
│ create_time         │      │
│ update_time         │      │
└─────────────────────┘      │
           │                 │
           │ 1:N             │
           │                 │
           ▼                 │
┌─────────────────────┐      │
│     station         │      │
├─────────────────────┤      │
│ id (PK)             │──────┘
│ sn_code (UNIQUE)    │
│ address             │
│ longitude           │
│ latitude            │
│ status              │
│ create_time         │
│ update_time         │
└─────────────────────┘
```

---

## 6. 数据库优化

### 6.1 查询优化
- **使用索引**：为常用查询字段建立索引
- **避免全表扫描**：使用 WHERE 条件限制查询范围
- **使用分页**：大数据量查询使用分页
- **使用缓存**：热数据使用 Redis 缓存

### 6.2 存储优化
- **字段类型选择**：选择合适的数据类型，减少存储空间
- **数据压缩**：对大文本字段进行压缩
- **定期清理**：删除过期数据，保持数据库性能

### 6.3 并发优化
- **使用事务**：确保数据一致性
- **使用锁**：防止并发冲突
- **使用分布式锁**：在分布式环境中使用 Redis 分布式锁

---

## 7. 数据库初始化脚本

### 7.1 创建数据库
```sql
CREATE DATABASE IF NOT EXISTS charging_user DEFAULT CHARSET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS charging_station DEFAULT CHARSET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS charging_order DEFAULT CHARSET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 7.2 创建表
```sql
-- 用户表
USE charging_user;
CREATE TABLE user (
  id BIGINT PRIMARY KEY,
  phone VARCHAR(20) UNIQUE NOT NULL,
  password VARCHAR(255) NOT NULL,
  nickname VARCHAR(50) NOT NULL,
  role INT DEFAULT 0,
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 充电桩表
USE charging_station;
CREATE TABLE station (
  id BIGINT PRIMARY KEY,
  sn_code VARCHAR(50) UNIQUE NOT NULL,
  address VARCHAR(255) NOT NULL,
  longitude DECIMAL(10, 6) NOT NULL,
  latitude DECIMAL(10, 6) NOT NULL,
  status INT DEFAULT 0,
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 订单表
USE charging_order;
CREATE TABLE order_info (
  id BIGINT PRIMARY KEY,
  order_no VARCHAR(50) UNIQUE NOT NULL,
  user_id BIGINT NOT NULL,
  station_id BIGINT NOT NULL,
  status INT DEFAULT 0,
  charge_type INT NOT NULL,
  charge_time INT NOT NULL,
  expected_amount DECIMAL(10, 2),
  actual_amount DECIMAL(10, 2),
  refund_amount DECIMAL(10, 2),
  actual_charge_time DECIMAL(10, 2),
  pay_time DATETIME,
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

---

**文档版本**：1.0  
**最后更新**：2024年3月16日  
**作者**：项目团队
