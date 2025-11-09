# Vblog JWT 身份验证系统文档

## 系统概述

基于 JWT (JSON Web Token) 的身份验证系统，使用 Redis 管理 Token 生命周期，支持自动失效机制。

## 技术栈

- **JWT库**: JJWT 0.12.3
- **加密算法**: HS384 (HMAC-SHA384)
- **Token存储**: Redis
- **失效机制**: 24小时未活动自动失效
- **Token有效期**: 24小时（86400000毫秒）

## 核心功能

### 1. Token 生成
- 登录成功后自动生成 JWT Token
- Token 包含用户ID和用户名
- Token 存储到 Redis，同时记录最后活动时间

### 2. Token 验证
- 使用 JWT 拦截器自动验证所有请求
- 验证 Token 格式、签名、过期时间
- 检查 Token 是否在 Redis 中（是否已登出）
- 检查是否24小时未活动

### 3. Token 刷新
- 生成新的 Token
- 删除旧 Token，保存新 Token 到 Redis

### 4. Token 失效条件

#### 自动失效
1. **Token 过期**: 超过24小时（配置的过期时间）
2. **24小时未活动**: 从最后一次操作起，24小时内没有任何活动

#### 手动失效
1. **用户登出**: 调用登出接口，从 Redis 删除 Token
2. **修改密码**: 密码修改后，当前 Token 自动失效

### 5. 权限控制

#### 注解说明
- `@RequireAuth`: 标记需要认证的接口，未登录无法访问
- `@GuestAllowed`: 标记游客可访问的接口，无需登录

#### 游客模式
未登录或 Token 失效后进入游客模式：
- ✅ 可以访问公开接口（标记 @GuestAllowed）
- ✅ 可以查看其他用户的笔记（读权限）
- ❌ 不能写笔记
- ❌ 不能删除笔记
- ❌ 不能修改资料

## API 接口

### 1. 登录（获取Token）

**接口**: `POST /api/user/login`

**请求体**:
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
        "token": "eyJhbGciOiJIUzM4NCJ9...",
        "user": {
            "id": 1,
            "username": "admin",
            "email": "admin@example.com",
            ...
        },
        "expiresIn": 86400000
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

### 2. 登出（使Token失效）

**接口**: `POST /api/user/logout`

**权限**: 需要认证（@RequireAuth）

**请求头**:
```
Authorization: Bearer {token}
```

**响应示例**:
```json
{
    "code": 200,
    "message": "登出成功",
    "data": null
}
```

**测试命令**:
```bash
curl -X POST http://localhost:8080/api/user/logout \
  -H "Authorization: Bearer YOUR_TOKEN"
```

---

### 3. 刷新Token

**接口**: `POST /api/user/refresh-token`

**权限**: 需要认证（@RequireAuth）

**请求头**:
```
Authorization: Bearer {token}
```

**响应示例**:
```json
{
    "code": 200,
    "message": "Token 刷新成功",
    "data": {
        "token": "eyJhbGciOiJIUzM4NCJ9...",
        "expiresIn": 86400000
    }
}
```

**测试命令**:
```bash
curl -X POST http://localhost:8080/api/user/refresh-token \
  -H "Authorization: Bearer YOUR_TOKEN"
```

---

### 4. 检查Token状态

**接口**: `GET /api/user/check-token`

**权限**: 需要认证（@RequireAuth）

**请求头**:
```
Authorization: Bearer {token}
```

**响应示例**:
```json
{
    "code": 200,
    "message": "Token 有效",
    "data": {
        "valid": true,
        "username": "admin",
        "lastActivity": 1762683319427
    }
}
```

**测试命令**:
```bash
curl -X GET http://localhost:8080/api/user/check-token \
  -H "Authorization: Bearer YOUR_TOKEN"
```

---

### 5. 获取当前登录用户

**接口**: `GET /api/user/current`

**权限**: 需要认证（@RequireAuth）

**请求头**:
```
Authorization: Bearer {token}
```

**响应示例**:
```json
{
    "code": 200,
    "message": "操作成功",
    "data": {
        "id": 1,
        "username": "admin",
        "email": "admin@example.com",
        ...
    }
}
```

**测试命令**:
```bash
curl -X GET http://localhost:8080/api/user/current \
  -H "Authorization: Bearer YOUR_TOKEN"
```

---

### 6. 查询用户信息（游客可访问）

**接口**: `GET /api/user/{username}`

**权限**: 游客可访问（@GuestAllowed）

**响应示例**:
```json
{
    "code": 200,
    "message": "操作成功",
    "data": {
        "id": 1,
        "username": "admin",
        ...
    }
}
```

**测试命令**:
```bash
# 无需 Token
curl -X GET http://localhost:8080/api/user/admin
```

---

### 7. 用户注册（游客可访问）

**接口**: `POST /api/user/register`

**权限**: 游客可访问（@GuestAllowed）

**请求体**:
```json
{
    "username": "newuser",
    "password": "pass123",
    "email": "user@example.com",
    "nickname": "新用户"
}
```

---

### 8. 修改密码（需认证）

**接口**: `POST /api/user/change-password`

**权限**: 需要认证（@RequireAuth）

**请求头**:
```
Authorization: Bearer {token}
```

**请求体**:
```json
{
    "username": "admin",
    "oldPassword": "123456",
    "newPassword": "newpass123"
}
```

**注意**: 修改密码后，当前 Token 会自动失效，需要重新登录。

---

## 错误响应

### 未登录
```json
{
    "code": 401,
    "message": "未登录，请先登录",
    "data": null
}
```

### Token 无效
```json
{
    "code": 401,
    "message": "Token 无效",
    "data": null
}
```

### Token 已过期
```json
{
    "code": 401,
    "message": "Token 已过期，请重新登录",
    "data": null
}
```

### 登录已失效（24小时未活动或已登出）
```json
{
    "code": 401,
    "message": "登录已失效，请重新登录",
    "data": null
}
```

---

## 前端集成指南

### 1. 登录并保存 Token

```javascript
// 登录
axios.post('/api/user/login', {
    username: 'admin',
    password: '123456'
})
.then(response => {
    // 保存 Token 到 localStorage
    const token = response.data.data.token;
    localStorage.setItem('jwt_token', token);
    
    // 配置 axios 默认请求头
    axios.defaults.headers.common['Authorization'] = 'Bearer ' + token;
});
```

### 2. 自动添加 Token 到所有请求

```javascript
// 配置 axios 拦截器
axios.interceptors.request.use(
    config => {
        const token = localStorage.getItem('jwt_token');
        if (token) {
            config.headers['Authorization'] = 'Bearer ' + token;
        }
        return config;
    },
    error => {
        return Promise.reject(error);
    }
);
```

### 3. 处理 401 错误（Token 失效）

```javascript
// 配置响应拦截器
axios.interceptors.response.use(
    response => response,
    error => {
        if (error.response && error.response.status === 401) {
            // Token 失效，清除本地 Token
            localStorage.removeItem('jwt_token');
            // 跳转到登录页
            window.location.href = '/login.html';
        }
        return Promise.reject(error);
    }
);
```

### 4. 定期刷新 Token（可选）

```javascript
// 每隔一定时间刷新 Token
setInterval(() => {
    axios.post('/api/user/refresh-token')
        .then(response => {
            const newToken = response.data.data.token;
            localStorage.setItem('jwt_token', newToken);
        })
        .catch(error => {
            // Token 刷新失败，可能已过期
            console.error('Token refresh failed:', error);
        });
}, 3600000); // 每小时刷新一次
```

### 5. 登出

```javascript
// 登出
axios.post('/api/user/logout')
    .then(response => {
        // 清除本地 Token
        localStorage.removeItem('jwt_token');
        // 跳转到登录页
        window.location.href = '/login.html';
    });
```

---

## Redis 存储结构

### Token 存储
- **Key**: `vblog:token:{username}`
- **Value**: JWT Token 字符串
- **TTL**: 86400000 毫秒（24小时）

### 最后活动时间
- **Key**: `vblog:activity:{username}`
- **Value**: 时间戳（毫秒）
- **TTL**: 86400000 毫秒（24小时）

---

## 配置说明

### application.properties

```properties
# JWT 配置
jwt.secret=VblogSecretKeyForJWTTokenGenerationAndValidation2025
jwt.expiration=86400000
jwt.token-prefix=Bearer 
jwt.header=Authorization

# Redis 配置
spring.data.redis.host=localhost
spring.data.redis.port=6379
spring.data.redis.database=0
spring.data.redis.timeout=10000ms
```

**重要提示**:
- `jwt.secret`: 密钥长度至少48字节（建议64字节以上）
- `jwt.expiration`: Token 有效期（毫秒）
- 生产环境请使用更强的密钥

---

## 测试页面

访问地址: `http://localhost:8080/jwt-test.html`

该页面提供了完整的 JWT 功能测试界面，包括：
- ✅ 登录获取 Token
- ✅ Token 状态检查
- ✅ Token 刷新
- ✅ 登出
- ✅ 访问受保护接口
- ✅ 访问公开接口（游客模式）
- ✅ 修改密码

---

## 安全建议

1. **HTTPS**: 生产环境必须使用 HTTPS，防止 Token 被窃取
2. **密钥管理**: 使用环境变量或密钥管理服务管理 JWT 密钥
3. **Token 有效期**: 根据业务需求调整 Token 有效期
4. **刷新机制**: 实现 Refresh Token 机制（可选）
5. **日志监控**: 记录登录/登出日志，监控异常行为
6. **IP 限制**: 可添加 IP 白名单或黑名单
7. **设备指纹**: 可绑定设备信息，防止 Token 被盗用

---

## 完整测试流程

```bash
# 1. 登录获取 Token
TOKEN=$(curl -s -X POST http://localhost:8080/api/user/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"123456"}' | \
  jq -r '.data.token')

# 2. 使用 Token 访问受保护接口
curl -X GET http://localhost:8080/api/user/current \
  -H "Authorization: Bearer $TOKEN"

# 3. 检查 Token 状态
curl -X GET http://localhost:8080/api/user/check-token \
  -H "Authorization: Bearer $TOKEN"

# 4. 刷新 Token
curl -X POST http://localhost:8080/api/user/refresh-token \
  -H "Authorization: Bearer $TOKEN"

# 5. 登出
curl -X POST http://localhost:8080/api/user/logout \
  -H "Authorization: Bearer $TOKEN"

# 6. 游客访问公开接口（无需 Token）
curl -X GET http://localhost:8080/api/user/admin
```

---

## 故障排查

### 问题1: Token 总是失效
**原因**: Redis 未启动或连接失败  
**解决**: 检查 Redis 容器状态
```bash
docker ps | grep redis
```

### 问题2: 跨域问题
**原因**: CORS 配置问题  
**解决**: 已在 WebConfig 中统一配置 CORS

### 问题3: Token 格式错误
**原因**: 请求头格式不正确  
**解决**: 确保使用 `Bearer {token}` 格式

### 问题4: 24小时未过但 Token 失效
**原因**: 最后活动时间超过24小时  
**解决**: 定期调用接口刷新活动时间，或调用刷新接口

