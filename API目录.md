# Vblog API 完整目录

基于 Kotlin DSL 的 API 自文档化系统

**Base URL**: `http://localhost:8080`

---

## 📦 用户 API (`/api/user`)

### 认证相关

| 方法 | 路径 | 描述 | 权限 |
|------|------|------|------|
| POST | `/api/user/login` | 用户登录，返回JWT Token | 游客可访问 |
| POST | `/api/user/register` | 用户注册 | 游客可访问 |
| POST | `/api/user/logout` | 用户登出，使Token失效 | 需要认证 |
| POST | `/api/user/refresh-token` | 刷新Token，获取新Token | 需要认证 |
| GET | `/api/user/check-token` | 检查Token是否有效 | 需要认证 |
| POST | `/api/user/parse-token` | 解析Token，获取uid和登录时间 | 需要认证 |

### 用户信息

| 方法 | 路径 | 描述 | 权限 |
|------|------|------|------|
| GET | `/api/user/current` | 获取当前登录用户信息 | 需要认证 |
| GET | `/api/user/{username}` | 根据用户名查询用户信息 | 游客可访问 |
| POST | `/api/user/change-password` | 修改密码（修改后Token失效） | 需要认证 |

---

## 📝 笔记 API (`/api/note`)

### 笔记管理

| 方法 | 路径 | 描述 | 权限 |
|------|------|------|------|
| POST | `/api/note/create` | 创建笔记 | 需要认证 |
| GET | `/api/note/{id}` | 获取笔记详情 | 游客可访问（公开笔记） |
| PUT | `/api/note/{id}` | 更新笔记 | 需要认证（只能修改自己的） |
| DELETE | `/api/note/{id}` | 删除笔记 | 需要认证（只能删除自己的） |

### 笔记列表

| 方法 | 路径 | 描述 | 权限 |
|------|------|------|------|
| GET | `/api/note/my` | 获取我的笔记列表 | 需要认证 |
| GET | `/api/note/user/{userId}` | 获取指定用户的笔记列表 | 游客可访问 |
| GET | `/api/note/public` | 获取所有公开笔记 | 游客可访问 |
| GET | `/api/note/count/{userId}` | 统计用户笔记数量 | 游客可访问 |

---

## 🧪 测试 API (`/api`)

| 方法 | 路径 | 描述 | 权限 |
|------|------|------|------|
| GET | `/api/test` | GET请求测试 | 游客可访问 |
| POST | `/api/test` | POST请求测试 | 游客可访问 |

---

## 📚 Kotlin DSL 使用示例

### 在代码中引用API

```kotlin
import org.example.api.VblogAPI

// 用户登录
val loginApi = VblogAPI.User.Login
println(loginApi)  // POST /api/user/login - 用户登录，返回JWT Token

// 创建笔记
val createNoteApi = VblogAPI.Note.Create
println(createNoteApi)  // POST /api/note/create - 创建笔记

// 获取指定笔记
val getNoteApi = VblogAPI.Note.GetById(1)
println(getNoteApi)  // GET /api/note/1 - 获取笔记详情

// 生成完整文档
VblogAPI.printAll()
```

### API 节点结构

```kotlin
object VblogAPI {
    object User {
        val Login = post("/api/user/login", "用户登录", guestAllowed = true)
        val Register = post("/api/user/register", "用户注册", guestAllowed = true)
        // ... 更多API
    }
    
    object Note {
        val Create = post("/api/note/create", "创建笔记", requireAuth = true)
        fun GetById(id: Long) = get("/api/note/$id", "获取笔记详情", guestAllowed = true)
        // ... 更多API
    }
}
```

---

## 🔑 权限说明

### 需要认证 (RequireAuth)
- 需要在请求头添加: `Authorization: Bearer {token}`
- Token 通过登录接口获取
- Token 有效期: 24小时
- 24小时未活动自动失效

### 游客可访问 (GuestAllowed)
- 无需Token即可访问
- 适用于公开内容（如查看公开笔记、用户信息等）

---

## 📝 请求示例

### 1. 登录获取Token

```bash
curl -X POST http://localhost:8080/api/user/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"123456"}'
```

响应：
```json
{
    "code": 200,
    "message": "登录成功",
    "data": {
        "token": "eyJhbGciOiJIUzM4NCJ9...",
        "user": {...},
        "expiresIn": 86400000
    }
}
```

### 2. 创建笔记

```bash
curl -X POST http://localhost:8080/api/note/create \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "title":"我的笔记",
    "content":"笔记内容",
    "status":1
  }'
```

### 3. 获取笔记详情（游客）

```bash
curl http://localhost:8080/api/note/1
```

### 4. 更新笔记

```bash
curl -X PUT http://localhost:8080/api/note/1 \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "title":"更新的标题",
    "content":"更新的内容",
    "status":1
  }'
```

### 5. 删除笔记

```bash
curl -X DELETE http://localhost:8080/api/note/1 \
  -H "Authorization: Bearer YOUR_TOKEN"
```

---

## 📂 API 文件位置

- **Kotlin DSL 定义**: `src/main/kotlin/org/example/api/VblogAPI.kt`
- **API Node基类**: `src/main/kotlin/org/example/api/ApiNode.kt`
- **本文档**: `API目录.md`

---

## ✨ 特性

1. **自文档化**: API定义即文档，代码即注释
2. **类型安全**: 使用Kotlin强类型系统
3. **树形结构**: 清晰的模块化组织
4. **DSL语法**: 简洁优雅的API定义方式
5. **集中管理**: 所有API统一在VblogAPI中定义
6. **易于维护**: 新增API只需在VblogAPI中添加

---

## 🔄 更新日志

### 2025-11-10
- ✅ 添加用户认证API（登录、注册、登出等）
- ✅ 添加Token管理API（刷新、解析、验证）
- ✅ 添加笔记CRUD API
- ✅ 添加笔记列表API（我的、公开、用户）
- ✅ 创建Kotlin DSL API文档系统

---

**最后更新**: 2025-11-10  
**API版本**: v1.0  
**文档状态**: ✅ 完整

