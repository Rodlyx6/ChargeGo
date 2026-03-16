# ChargeGo 共享充电桩平台 - API 接口文档

## 1. API 概述

### 1.1 基本信息
- **API 基础 URL**：`http://localhost:8000`
- **API 版本**：v1.0
- **数据格式**：JSON
- **字符编码**：UTF-8
- **认证方式**：JWT Token

### 1.2 请求头
```
Content-Type: application/json
Authorization: Bearer <token>
X-User-Id: <userId>
```

### 1.3 响应格式
```json
{
  "code": 200,
  "msg": "成功",
  "data": {}
}
```

---

## 2. 用户认证接口

### 2.1 用户注册

**请求方法**：POST  
**请求路径**：`/user/register`  
**认证**：否

#### 请求体
```json
{
  "phone": "13800000000",
  "password": "123456",
  "nickname": "用户昵称"
}
```

#### 响应示例
```json
{
  "code": 200,
  "msg": "注册成功",
  "data": null
}
```

#### 错误响应
```json
{
  "code": 400,
  "msg": "手机号已存在",
  "data": null
}
```

---

### 2.2 用户登录

**请求方法**：POST  
**请求路径**：`/user/login`  
**认证**：否

#### 请求体
```json
{
  "phone": "13800000000",
  "password": "123456"
}
```

#### 响应示例
```json
{
  "code": 200,
  "msg": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "user": {
      "userId": "2031229876161310722",
      "phone": "13800000000",
      "nickname": "用户昵称",
      "role": 0
    }
  }
}
```

---

## 3. 充电桩接口

### 3.1 附近搜索

**请求方法**：POST  
**请求路径**：`/station/nearby`  
**认证**：否

#### 请求体
```json
{
  "longitude": 113.943100,
  "latitude": 22.541100,
  "radiusMeters": 5000
}
```

#### 响应示例
```json
{
  "code": 200,
  "msg": "查询成功",
  "data": [
    {
      "id": "101",
      "snCode": "SN-A001",
      "address": "科技园A栋地下车库",
      "longitude": 113.943100,
      "latitude": 22.541100,
      "status": 0,
      "distance": 1200
    },
    {
      "id": "102",
      "snCode": "SN-B002",
      "address": "商业中心B栋停车场",
      "longitude": 113.950000,
      "latitude": 22.545000,
      "status": 1,
      "distance": 2500
    }
  ]
}
```

#### 字段说明
| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | String | 充电桩ID |
| snCode | String | 充电桩编号 |
| address | String | 地址 |
| longitude | Double | 经度 |
| latitude | Double | 纬度 |
| status | Integer | 状态（0空闲，1预约中，2充电中，3故障） |
| distance | Integer | 距离（米） |

---

### 3.2 查询所有充电桩（管理员）

**请求方法**：GET  
**请求路径**：`/admin/station/list`  
**认证**：是（管理员）

#### 请求参数
```
pageNum: 1
pageSize: 10
```

#### 响应示例
```json
{
  "code": 200,
  "msg": "查询成功",
  "data": {
    "records": [
      {
        "id": "101",
        "snCode": "SN-A001",
        "address": "科技园A栋地下车库",
        "longitude": 113.943100,
        "latitude": 22.541100,
        "status": 0,
        "createTime": "2026-03-16T10:00:00",
        "updateTime": "2026-03-16T10:00:00"
      }
    ],
    "total": 100,
    "pages": 10,
    "current": 1,
    "size": 10
  }
}
```

---

### 3.3 新增充电桩（管理员）

**请求方法**：POST  
**请求路径**：`/admin/station/add`  
**认证**：是（管理员）

#### 请求体
```json
{
  "snCode": "SN-C003",
  "address": "新增充电桩地址",
  "longitude": 113.960000,
  "latitude": 22.550000
}
```

#### 响应示例
```json
{
  "code": 200,
  "msg": "新增成功",
  "data": "103"
}
```

---

### 3.4 编辑充电桩（管理员）

**请求方法**：PUT  
**请求路径**：`/admin/station/update/{id}`  
**认证**：是（管理员）

#### 请求体
```json
{
  "address": "更新后的地址",
  "longitude": 113.960000,
  "latitude": 22.550000
}
```

#### 响应示例
```json
{
  "code": 200,
  "msg": "更新成功",
  "data": null
}
```

---

### 3.5 删除充电桩（管理员）

**请求方法**：DELETE  
**请求路径**：`/admin/station/delete/{id}`  
**认证**：是（管理员）

#### 响应示例
```json
{
  "code": 200,
  "msg": "删除成功",
  "data": null
}
```

---

## 4. 订单接口

### 4.1 创建订单

**请求方法**：POST  
**请求路径**：`/order/create`  
**认证**：是

#### 请求体
```json
{
  "stationId": "101",
  "chargeType": 1,
  "chargeTime": 2
}
```

#### 响应示例
```json
{
  "code": 200,
  "msg": "预约成功",
  "data": "1773667959433C77C"
}
```

#### 字段说明
| 字段名 | 类型 | 说明 |
|--------|------|------|
| stationId | String | 充电桩ID |
| chargeType | Integer | 充电类型（1快充，2普通充电，3慢充） |
| chargeTime | Integer | 预约充电时间（小时） |

---

### 4.2 支付订单

**请求方法**：POST  
**请求路径**：`/order/pay`  
**认证**：是

#### 请求体
```json
{
  "orderNo": "1773667959433C77C"
}
```

#### 响应示例
```json
{
  "code": 200,
  "msg": "支付成功",
  "data": null
}
```

---

### 4.3 取消支付

**请求方法**：POST  
**请求路径**：`/order/cancelPayment`  
**认证**：是

#### 请求体
```json
{
  "orderNo": "1773667959433C77C"
}
```

#### 响应示例
```json
{
  "code": 200,
  "msg": "取消成功",
  "data": null
}
```

---

### 4.4 结束充电

**请求方法**：POST  
**请求路径**：`/order/cancelOrder`  
**认证**：是

#### 请求体
```json
{
  "orderNo": "1773667959433C77C"
}
```

#### 响应示例
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": null
}
```

---

### 4.5 查询我的订单列表

**请求方法**：GET  
**请求路径**：`/order/list`  
**认证**：是

#### 请求参数
```
pageNum: 1
pageSize: 10
```

#### 响应示例
```json
{
  "code": 200,
  "msg": "查询成功",
  "data": [
    {
      "orderNo": "1773667959433C77C",
      "userId": "2031229876161310722",
      "stationId": "101",
      "status": 2,
      "statusDesc": "已完成",
      "chargeType": 1,
      "chargeTypeDesc": "快充",
      "chargeTime": 1,
      "expectedAmount": 10.00,
      "actualAmount": 0.20,
      "refundAmount": 9.80,
      "actualChargeTime": 0.02,
      "payTime": "2026-03-16T21:33:00",
      "createTime": "2026-03-16T21:32:39",
      "updateTime": "2026-03-16T21:35:00"
    }
  ]
}
```

---

### 4.6 查询订单详情

**请求方法**：GET  
**请求路径**：`/order/{orderNo}`  
**认证**：是

#### 响应示例
```json
{
  "code": 200,
  "msg": "查询成功",
  "data": {
    "orderNo": "1773667959433C77C",
    "userId": "2031229876161310722",
    "stationId": "101",
    "status": 2,
    "statusDesc": "已完成",
    "chargeType": 1,
    "chargeTypeDesc": "快充",
    "chargeTime": 1,
    "expectedAmount": 10.00,
    "actualAmount": 0.20,
    "refundAmount": 9.80,
    "actualChargeTime": 0.02,
    "payTime": "2026-03-16T21:33:00",
    "createTime": "2026-03-16T21:32:39",
    "updateTime": "2026-03-16T21:35:00"
  }
}
```

---

### 4.7 查询所有订单（管理员）

**请求方法**：GET  
**请求路径**：`/admin/order/list`  
**认证**：是（管理员）

#### 请求参数
```
pageNum: 1
pageSize: 10
status: (可选，0待支付，1充电中，2已完成，3已取消)
```

#### 响应示例
```json
{
  "code": 200,
  "msg": "查询成功",
  "data": {
    "records": [
      {
        "orderNo": "1773667959433C77C",
        "userId": "2031229876161310722",
        "stationId": "101",
        "status": 2,
        "statusDesc": "已完成",
        "chargeType": 1,
        "chargeTypeDesc": "快充",
        "chargeTime": 1,
        "expectedAmount": 10.00,
        "actualAmount": 0.20,
        "refundAmount": 9.80,
        "actualChargeTime": 0.02,
        "payTime": "2026-03-16T21:33:00",
        "createTime": "2026-03-16T21:32:39",
        "updateTime": "2026-03-16T21:35:00"
      }
    ],
    "total": 1000,
    "pages": 100,
    "current": 1,
    "size": 10
  }
}
```

---

### 4.8 查询订单统计（管理员）

**请求方法**：GET  
**请求路径**：`/admin/order/stats`  
**认证**：是（管理员）

#### 响应示例
```json
{
  "code": 200,
  "msg": "查询成功",
  "data": {
    "totalOrders": 1000,
    "pendingPayment": 50,
    "charging": 100,
    "completed": 800,
    "cancelled": 50,
    "totalRevenue": 5000.00,
    "averageOrderAmount": 5.00
  }
}
```

---

## 5. 用户管理接口（管理员）

### 5.1 查询用户列表

**请求方法**：GET  
**请求路径**：`/admin/user/list`  
**认证**：是（管理员）

#### 请求参数
```
pageNum: 1
pageSize: 10
role: (可选，0普通用户，1管理员)
```

#### 响应示例
```json
{
  "code": 200,
  "msg": "查询成功",
  "data": {
    "records": [
      {
        "userId": "2031229876161310722",
        "phone": "13800000000",
        "nickname": "超级管理员",
        "role": 1,
        "createTime": "2026-03-16T10:00:00",
        "updateTime": "2026-03-16T10:00:00"
      }
    ],
    "total": 500,
    "pages": 50,
    "current": 1,
    "size": 10
  }
}
```

---

### 5.2 查询用户统计

**请求方法**：GET  
**请求路径**：`/admin/user/stats`  
**认证**：是（管理员）

#### 响应示例
```json
{
  "code": 200,
  "msg": "查询成功",
  "data": {
    "totalUsers": 500,
    "normalUsers": 495,
    "adminUsers": 5
  }
}
```

---

## 6. 错误码说明

| 错误码 | 说明 |
|--------|------|
| 200 | 成功 |
| 400 | 请求参数错误 |
| 401 | 未认证（缺少 Token） |
| 403 | 无权限（权限不足） |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

---

## 7. 充电类型说明

| 充电类型 | 代码 | 单价 | 说明 |
|---------|------|------|------|
| 快充 | 1 | 10元/小时 | 30分钟充满 |
| 普通充电 | 2 | 5元/小时 | 2小时充满 |
| 慢充 | 3 | 2元/小时 | 8小时充满 |

---

## 8. 订单状态说明

| 订单状态 | 代码 | 说明 |
|---------|------|------|
| 待支付 | 0 | 用户已预约，等待支付（15分钟内必须支付） |
| 充电中 | 1 | 用户已支付，正在充电 |
| 已完成 | 2 | 充电已结束，可查看退款信息 |
| 已取消 | 3 | 订单已取消 |

---

## 9. 充电桩状态说明

| 充电桩状态 | 代码 | 说明 |
|----------|------|------|
| 空闲 | 0 | 充电桩可用 |
| 预约中 | 1 | 充电桩已被预约 |
| 充电中 | 2 | 充电桩正在充电 |
| 故障 | 3 | 充电桩故障 |

---

## 10. 用户角色说明

| 用户角色 | 代码 | 说明 |
|---------|------|------|
| 普通用户 | 0 | 可以搜索、预约、支付充电 |
| 管理员 | 1 | 可以管理充电桩、订单、用户 |

---

**文档版本**：1.0  
**最后更新**：2024年3月16日  
**作者**：项目团队
