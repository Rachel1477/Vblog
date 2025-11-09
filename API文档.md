# Vblog 用户登录注册系统 API 文档

## 系统概述

基于 Spring Boot + MyBatis 的用户登录注册系统，使用 SHA-256 加密存储密码。

## 数据库配置

- **数据库**: MySQL 8.0
- **端口**: 10000 (映射到容器内 3306)
- **数据库名**: vblog
- **用户名**: root
- **密码**: 123456

## 用户表结构

```sql
CREATE TABLE `user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名',
    `password` VARCHAR(64) NOT NULL COMMENT '密码（SHA-256加密）',
    `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    `nickname` VARCHAR(50) DEFAULT NULL COMMENT '昵称',
    `avatar` VARCHAR(255) DEFAULT NULL COMMENT '头像URL',
    `status` TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-正常',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    UNIQUE KEY `uk_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

## API 接口

### 1. 用户登录

**接口地址**: `POST /api/user/login`

**请求参数**:
```json
{
    "username": "admin",
    "password": "123456"
}
```

**响应示例**:
```json
{
    "code": 200,
    "message": "登录成功",
    "data": {
        "id": 1,
        "username": "admin",
        "password": null,
        "email": "admin@example.com",
        "nickname": "管理员",
        "avatar": null,
        "status": 1,
        "createTime": "2025-11-09T17:47:42",
        "updateTime": "2025-11-09T17:47:42"
    }
}
```

**测试命令**:
```bash
curl -X POST http://localhost:8080/api/user/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"123456"}'
```

---

### 2. 用户注册

**接口地址**: `POST /api/user/register`

**请求参数**:
```json
{
    "username": "testuser",       // 必填，3-20个字符
    "password": "test123",        // 必填，至少6个字符
    "email": "test@example.com",  // 可选
    "nickname": "测试用户"         // 可选，默认为用户名
}
```

**响应示例**:
```json
{
    "code": 200,
    "message": "注册成功",
    "data": {
        "id": 2,
        "username": "testuser",
        "password": null,
        "email": "test@example.com",
        "nickname": "测试用户",
        "avatar": null,
        "status": 1,
        "createTime": "2025-11-09T17:49:42",
        "updateTime": "2025-11-09T17:49:42"
    }
}
```

**测试命令**:
```bash
curl -X POST http://localhost:8080/api/user/register \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"test123","email":"test@example.com","nickname":"测试用户"}'
```

---

### 3. 修改密码

**接口地址**: `POST /api/user/change-password`

**请求参数**:
```json
{
    "username": "testuser",
    "oldPassword": "test123",
    "newPassword": "newpass123"   // 至少6个字符
}
```

**响应示例**:
```json
{
    "code": 200,
    "message": "密码修改成功",
    "data": true
}
```

**测试命令**:
```bash
curl -X POST http://localhost:8080/api/user/change-password \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","oldPassword":"test123","newPassword":"newpass123"}'
```

---

### 4. 查询用户信息

**接口地址**: `GET /api/user/{username}`

**请求参数**: 路径参数 username

**响应示例**:
```json
{
    "code": 200,
    "message": "操作成功",
    "data": {
        "id": 1,
        "username": "admin",
        "password": null,
        "email": "admin@example.com",
        "nickname": "管理员",
        "avatar": null,
        "status": 1,
        "createTime": "2025-11-09T17:47:42",
        "updateTime": "2025-11-09T17:47:42"
    }
}
```

**测试命令**:
```bash
curl http://localhost:8080/api/user/admin
```

---

## 错误响应格式

```json
{
    "code": 500,
    "message": "错误信息描述",
    "data": null
}
```

## 常见错误信息

- `用户名不能为空`
- `密码不能为空`
- `用户不存在`
- `密码错误`
- `用户已被禁用`
- `用户名已存在`
- `邮箱已被使用`
- `用户名长度必须在3-20个字符之间`
- `密码长度不能少于6个字符`
- `旧密码错误`

## 密码加密

系统使用 **SHA-256** 算法加密密码：

- 加密工具类: `org.example.util.PasswordUtil`
- 加密方法: `PasswordUtil.encrypt(String password)`
- 验证方法: `PasswordUtil.verify(String inputPassword, String storedPassword)`

**示例**:
```
明文: 123456
SHA-256: 8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92
```

## 前端测试页面

访问地址: `http://localhost:8080/login-test.html`

该页面提供了完整的前端测试界面，包括：
- 用户登录
- 用户注册
- 修改密码
- 查询用户信息

## 启动说明

1. **启动数据库**:
```bash
docker-compose up -d
```

2. **启动应用**:
```bash
mvn spring-boot:run
```

3. **停止数据库**:
```bash
docker-compose down
```

## 预设测试账号

- **用户名**: admin
- **密码**: 123456
- **邮箱**: admin@example.com

## 技术栈

- Spring Boot 3.2.0
- MyBatis 3.0.3
- MySQL 8.0
- Redis (已配置，待使用)
- Maven
- Docker & Docker Compose

