# U Ju 活动管理平台 — 技术方案 v1.0

## 1. 技术选型

### 1.1 技术栈总览

| 层级 | 技术 | 版本 | 说明 |
|------|------|------|------|
| 后端框架 | Spring Boot | 3.2.5 | Java 17+ RESTful API |
| ORM | MyBatis-Plus | 3.5.6 | 增强版 MyBatis |
| 数据库 | MySQL | 8.0 | 关系型数据存储 |
| 缓存 | Redis | 7.x | 会话、验证码、热点数据 |
| 认证 | Spring Security + JWT | jjwt 0.12.5 | Token 认证机制 |
| API文档 | Knife4j | 4.4.0 | Swagger 增强 |
| 工具库 | Hutool | 5.8.26 | Java 工具集 |
| 后台前端 | Vue3 + Element Plus | 3.4 / 2.7 | PC端管理系统 |
| 构建工具 | Vite | 5.x | 前端构建 |
| 状态管理 | Pinia | 2.x | Vue3 状态管理 |
| 移动端 | UniApp (Vue3) | 3.x | 小程序 + H5 |
| 文件存储 | 本地 / 阿里云 OSS | - | 图片和文件上传 |

### 1.2 项目模块划分

```
u-ju/
├── u-ju-server/          # Java Spring Boot 后端
│   ├── src/main/java/com/uju/platform/
│   │   ├── config/       # 配置类
│   │   ├── common/       # 公共组件
│   │   ├── util/         # 工具类
│   │   ├── user/         # 用户模块
│   │   ├── event/        # 活动模块
│   │   ├── registration/ # 报名模块
│   │   ├── order/        # 订单模块
│   │   ├── certification/# 认证模块
│   │   ├── notification/ # 通知模块
│   │   └── admin/        # 管理模块
│   └── src/main/resources/
│       ├── application.yml
│       └── db/init.sql
├── u-ju-admin/           # Vue3 后台管理系统
│   ├── src/
│   │   ├── api/          # API 接口
│   │   ├── views/        # 页面组件
│   │   ├── router/       # 路由
│   │   ├── stores/       # 状态管理
│   │   ├── utils/        # 工具函数
│   │   └── layout/       # 布局组件
│   └── vite.config.ts
└── u-ju-uniapp/          # UniApp 小程序 + H5
    ├── src/
    │   ├── pages/        # 页面
    │   ├── api/          # API 接口
    │   ├── stores/       # 状态管理
    │   ├── utils/        # 工具函数
    │   └── components/   # 公共组件
    ├── manifest.json
    └── pages.json
```

---

## 2. 数据库设计

### 2.1 ER 关系图

```
users ──1:N──> organizer_certifications
users ──1:N──> events (发布者)
users ──1:N──> registrations
users ──1:N──> orders
users ──1:N──> favorites
users ──1:N──> notifications
events ──1:N──> tickets
events ──1:N──> registrations
tickets ──1:N──> registrations
registrations ──1:1──> orders
```

### 2.2 核心表结构

#### users 用户表

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT PK | 主键 |
| username | VARCHAR(50) UK | 用户名 |
| phone | VARCHAR(20) UK | 手机号 |
| password | VARCHAR(100) | 密码(bcrypt) |
| real_name | VARCHAR(50) | 真实姓名 |
| avatar | VARCHAR(255) | 头像URL |
| email | VARCHAR(100) | 邮箱 |
| gender | TINYINT | 性别 0未知 1男 2女 |
| birthday | DATE | 出生日期 |
| role | VARCHAR(20) | 角色: PARTICIPANT/ORGANIZER/ADMIN |
| status | TINYINT | 状态 0禁用 1正常 |
| created_at | DATETIME | 创建时间 |
| updated_at | DATETIME | 更新时间 |
| deleted | TINYINT | 逻辑删除 0未删 1已删 |

#### organizer_certifications 企业认证表

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT PK | 主键 |
| user_id | BIGINT FK | 用户ID |
| company_name | VARCHAR(100) | 企业名称 |
| credit_code | VARCHAR(50) | 统一社会信用代码 |
| business_license | VARCHAR(255) | 营业执照URL |
| company_address | VARCHAR(255) | 企业地址 |
| contact_person | VARCHAR(50) | 联系人 |
| contact_phone | VARCHAR(20) | 联系电话 |
| status | TINYINT | 0待审核 1通过 2拒绝 |
| review_remark | VARCHAR(255) | 审核备注 |
| reviewer_id | BIGINT | 审核人ID |
| reviewed_at | DATETIME | 审核时间 |
| created_at | DATETIME | 创建时间 |
| updated_at | DATETIME | 更新时间 |

#### events 活动表

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT PK | 主键 |
| publisher_id | BIGINT FK | 发布者用户ID |
| title | VARCHAR(100) | 活动标题 |
| cover_images | JSON | 封面图URL数组 |
| category | VARCHAR(20) | 分类 |
| tags | JSON | 标签数组 |
| summary | VARCHAR(200) | 简介 |
| detail | TEXT | 详情(富文本) |
| start_time | DATETIME | 开始时间 |
| end_time | DATETIME | 结束时间 |
| registration_deadline | DATETIME | 报名截止时间 |
| location_type | TINYINT | 0线下 1线上 2混合 |
| province | VARCHAR(20) | 省 |
| city | VARCHAR(20) | 市 |
| district | VARCHAR(20) | 区 |
| address | VARCHAR(255) | 详细地址 |
| is_free | TINYINT | 0付费 1免费 |
| min_participants | INT | 最少人数 |
| max_participants | INT | 最大人数 |
| need_review | TINYINT | 0自动通过 1需审核 |
| status | TINYINT | 0草稿 1已发布 2已下架 3已取消 |
| is_public | TINYINT | 0否 1是 |
| contact | VARCHAR(100) | 联系方式 |
| refund_policy | VARCHAR(255) | 退款政策 |
| is_recommended | TINYINT | 0否 1推荐 |
| view_count | INT | 浏览量 |
| created_at | DATETIME | 创建时间 |
| updated_at | DATETIME | 更新时间 |
| deleted | TINYINT | 逻辑删除 |

#### tickets 票型表

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT PK | 主键 |
| event_id | BIGINT FK | 活动ID |
| name | VARCHAR(50) | 票型名称 |
| price | DECIMAL(10,2) | 价格 |
| quantity | INT | 总数量 |
| sold_quantity | INT | 已售数量 |
| description | VARCHAR(255) | 说明 |
| created_at | DATETIME | 创建时间 |
| updated_at | DATETIME | 更新时间 |

#### registrations 报名表

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT PK | 主键 |
| user_id | BIGINT FK | 用户ID |
| event_id | BIGINT FK | 活动ID |
| ticket_id | BIGINT FK | 票型ID(免费活动为空) |
| form_data | JSON | 报名表单数据 |
| status | TINYINT | 0待审核 1已通过 2已拒绝 3已取消 |
| ticket_code | VARCHAR(50) UK | 电子票码 |
| created_at | DATETIME | 创建时间 |
| updated_at | DATETIME | 更新时间 |
| deleted | TINYINT | 逻辑删除 |

#### orders 订单表

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT PK | 主键 |
| order_no | VARCHAR(32) UK | 订单号 |
| user_id | BIGINT FK | 用户ID |
| registration_id | BIGINT FK | 报名ID |
| event_id | BIGINT FK | 活动ID |
| ticket_id | BIGINT FK | 票型ID |
| amount | DECIMAL(10,2) | 金额 |
| pay_method | TINYINT | 0微信 1支付宝 |
| pay_time | DATETIME | 支付时间 |
| status | TINYINT | 0待支付 1支付中 2已支付 3已取消 4已退款 5退款中 |
| expire_at | DATETIME | 过期时间(15分钟) |
| created_at | DATETIME | 创建时间 |
| updated_at | DATETIME | 更新时间 |

#### favorites 收藏表

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT PK | 主键 |
| user_id | BIGINT FK | 用户ID |
| event_id | BIGINT FK | 活动ID |
| created_at | DATETIME | 创建时间 |

#### notifications 通知表

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT PK | 主键 |
| user_id | BIGINT FK | 用户ID |
| title | VARCHAR(100) | 标题 |
| content | TEXT | 内容 |
| type | TINYINT | 0系统 1活动 2支付 |
| is_read | TINYINT | 0未读 1已读 |
| created_at | DATETIME | 创建时间 |

---

## 3. API 设计

### 3.1 用户模块 `/api/users`

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| POST | /api/users/register | 参与者注册 | 否 |
| POST | /api/users/login | 密码登录 | 否 |
| POST | /api/users/sms-login | 验证码登录 | 否 |
| GET | /api/users/me | 获取当前用户 | 是 |
| PUT | /api/users/me | 更新个人资料 | 是 |
| PUT | /api/users/password | 修改密码 | 是 |
| POST | /api/users/avatar | 上传头像 | 是 |

### 3.2 认证模块 `/api/certifications`

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| POST | /api/certifications | 提交企业认证 | 是 |
| GET | /api/certifications/me | 查看我的认证 | 是 |

### 3.3 活动模块 `/api/events`

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| GET | /api/events | 活动列表(分页/筛选) | 否 |
| GET | /api/events/{id} | 活动详情 | 否 |
| POST | /api/events | 发布活动 | 是(发布者) |
| PUT | /api/events/{id} | 编辑活动 | 是(发布者) |
| PUT | /api/events/{id}/status | 修改活动状态 | 是(发布者) |
| GET | /api/events/my | 我发布的活动 | 是 |
| POST | /api/events/{id}/favorite | 收藏/取消收藏 | 是 |
| GET | /api/events/favorites | 我的收藏列表 | 是 |

### 3.4 报名模块 `/api/registrations`

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| POST | /api/registrations | 提交报名 | 是 |
| GET | /api/registrations/my | 我的报名列表 | 是 |
| GET | /api/registrations/{id} | 报名详情 | 是 |
| PUT | /api/registrations/{id}/cancel | 取消报名 | 是 |
| GET | /api/registrations/event/{eventId} | 活动报名列表 | 是(发布者) |
| PUT | /api/registrations/{id}/review | 审核报名 | 是(发布者) |

### 3.5 订单模块 `/api/orders`

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| POST | /api/orders | 创建订单 | 是 |
| GET | /api/orders/my | 我的订单列表 | 是 |
| GET | /api/orders/{id} | 订单详情 | 是 |
| POST | /api/orders/{id}/pay | 支付订单 | 是 |
| POST | /api/orders/{id}/cancel | 取消订单 | 是 |
| POST | /api/orders/{id}/refund | 申请退款 | 是 |

### 3.6 通知模块 `/api/notifications`

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| GET | /api/notifications | 通知列表 | 是 |
| GET | /api/notifications/unread-count | 未读数量 | 是 |
| PUT | /api/notifications/{id}/read | 标记已读 | 是 |
| PUT | /api/notifications/read-all | 全部已读 | 是 |

### 3.7 管理模块 `/api/admin`

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| POST | /api/admin/login | 管理员登录 | 否 |
| GET | /api/admin/dashboard | 数据概览 | 是(管理员) |
| GET | /api/admin/users | 用户列表 | 是(管理员) |
| PUT | /api/admin/users/{id}/status | 修改用户状态 | 是(管理员) |
| GET | /api/admin/certifications | 认证列表 | 是(管理员) |
| PUT | /api/admin/certifications/{id}/review | 审核认证 | 是(管理员) |
| GET | /api/admin/events | 活动列表 | 是(管理员) |
| PUT | /api/admin/events/{id}/review | 审核活动 | 是(管理员) |
| GET | /api/admin/orders | 订单列表 | 是(管理员) |
| PUT | /api/admin/orders/{id}/refund | 审核退款 | 是(管理员) |

### 3.8 文件模块 `/api/files`

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| POST | /api/files/upload | 上传文件 | 是 |

---

## 4. 架构设计

### 4.1 后端分层架构

```
Controller → Service → Mapper(Entity)
    ↓           ↓
  DTO/VO    BusinessException
```

- **Controller**: 接收请求，参数校验，调用 Service
- **Service**: 业务逻辑，事务管理
- **Mapper**: 数据访问，MyBatis-Plus
- **DTO**: 请求参数对象
- **VO**: 响应视图对象
- **Entity**: 数据库实体

### 4.2 认证授权流程

```
1. 用户登录 → JWT Token(含userId, role)
2. 请求携带 Authorization: Bearer <token>
3. JwtAuthenticationFilter 解析验证Token
4. SecurityContext 存储认证信息
5. @PreAuthorize 角色权限校验
```

### 4.3 统一响应格式

```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```

### 4.4 前端架构

**Admin (Vue3)**:
- 路由守卫: 未登录跳转 /login
- Axios 拦截器: 自动携带 Token, 401 跳转登录
- Pinia Store: 用户状态管理
- Element Plus: UI 组件库

**UniApp**:
- 页面路由: pages.json 配置
- 请求封装: uni.request + Token 拦截
- Pinia Store: 用户状态管理
- 条件编译: #ifdef MP-WEIXIN / #ifdef H5

---

## 5. 安全设计

- 密码: BCrypt 加密存储
- JWT: RS256 签名, 24h 过期
- 接口: Spring Security + @PreAuthorize
- SQL: MyBatis-Plus 参数化查询, 防注入
- XSS: 输入过滤 + 输出转义
- CORS: 配置允许的域名
- 文件上传: 类型/大小限制
