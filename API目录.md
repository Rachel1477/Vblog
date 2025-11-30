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

## 📤 文件上传下载 API (`/api/upload`)

### 文件上传

| 方法 | 路径 | 描述 | 权限 |
|------|------|------|------|
| POST | `/api/upload/image` | 上传图片（JPEG, PNG, GIF, WEBP, BMP） | 需要认证 |
| POST | `/api/upload/video` | 上传视频（MP4, AVI, MOV, WMV, FLV, WEBM） | 需要认证 |
| POST | `/api/upload/file` | 上传通用文件（所有类型） | 需要认证 |

### 文件下载与管理

| 方法 | 路径 | 描述 | 权限 |
|------|------|------|------|
| GET | `/api/upload/download/{id}` | 下载文件（带权限验证） | 需要认证 |
| GET | `/api/upload/{id}` | 获取文件信息 | 需要认证（只能查看自己的） |
| DELETE | `/api/upload/{id}` | 删除文件 | 需要认证（只能删除自己的） |

### 文件列表

| 方法 | 路径 | 描述 | 权限 |
|------|------|------|------|
| GET | `/api/upload/my` | 获取我的所有文件列表 | 需要认证 |
| GET | `/api/upload/my/type` | 根据文件类型获取我的文件列表 | 需要认证 |
| GET | `/api/upload/my/count` | 统计我的文件数量 | 需要认证 |

**注意**：
- 所有上传文件最大大小限制：50MB
- 上传的文件可以通过静态资源路径直接访问：`/uploads/images/xxx.png`、`/uploads/videos/xxx.mp4`、`/uploads/files/xxx.pdf`
- 下载接口提供带权限验证的文件下载，确保用户只能下载自己的文件

---

## 📅 计划 API (`/api/plan`)

### 计划管理

| 方法 | 路径 | 描述 | 权限 |
|------|------|------|------|
| POST | `/api/plan/create` | 创建计划 | 需要认证 |
| GET | `/api/plan/{id}` | 获取计划详情 | 需要认证（只能查看自己的） |
| PUT | `/api/plan/{id}` | 更新计划 | 需要认证（只能修改自己的） |
| DELETE | `/api/plan/{id}` | 删除计划 | 需要认证（只能删除自己的） |

### 计划列表

| 方法 | 路径 | 描述 | 权限 |
|------|------|------|------|
| GET | `/api/plan/my` | 获取我的计划列表 | 需要认证 |
| GET | `/api/plan/my/range` | 根据日期范围获取我的计划列表 | 需要认证 |
| GET | `/api/plan/my/count` | 统计我的计划数量 | 需要认证 |

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

### 6. 创建计划

```bash
curl -X POST http://localhost:8080/api/plan/create \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "title":"明天的会议",
    "content":"上午10点与客户开会讨论项目进展",
    "planTime":"2025-11-17T10:00:00",
    "status":0
  }'
```

响应：
```json
{
    "code": 200,
    "message": "计划创建成功",
    "data": {
        "id": 1,
        "userId": 1,
        "title": "明天的会议",
        "content": "上午10点与客户开会讨论项目进展",
        "planTime": "2025-11-17T10:00:00",
        "status": 0,
        "createTime": "2025-11-16T15:30:00",
        "updateTime": "2025-11-16T15:30:00"
    }
}
```

### 7. 获取计划详情

```bash
curl http://localhost:8080/api/plan/1 \
  -H "Authorization: Bearer YOUR_TOKEN"
```

### 8. 更新计划

```bash
curl -X PUT http://localhost:8080/api/plan/1 \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "title":"明天的会议（已更新）",
    "content":"上午10点与客户开会讨论项目进展，地点：会议室A",
    "planTime":"2025-11-17T10:00:00",
    "status":0
  }'
```

### 9. 删除计划

```bash
curl -X DELETE http://localhost:8080/api/plan/1 \
  -H "Authorization: Bearer YOUR_TOKEN"
```

### 10. 获取我的计划列表

```bash
curl http://localhost:8080/api/plan/my \
  -H "Authorization: Bearer YOUR_TOKEN"
```

### 11. 根据日期范围获取计划

```bash
curl "http://localhost:8080/api/plan/my/range?startTime=2025-11-17T00:00:00&endTime=2025-11-17T23:59:59" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

### 12. 统计我的计划数量

```bash
curl http://localhost:8080/api/plan/my/count \
  -H "Authorization: Bearer YOUR_TOKEN"
```

### 13. 上传图片

```bash
curl -X POST http://localhost:8080/api/upload/image \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -F "file=@/path/to/image.jpg"
```

响应：
```json
{
    "code": 200,
    "message": "图片上传成功",
    "data": {
        "id": 1,
        "userId": 1,
        "url": "/uploads/images/abc123def456.jpg",
        "path": "/home/user/project/uploads/images/abc123def456.jpg",
        "size": 102400,
        "contentType": "image/jpeg",
        "createTime": "2025-11-16T15:30:00"
    }
}
```

### 14. 上传视频

```bash
curl -X POST http://localhost:8080/api/upload/video \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -F "file=@/path/to/video.mp4"
```

### 15. 上传通用文件

```bash
curl -X POST http://localhost:8080/api/upload/file \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -F "file=@/path/to/document.pdf"
```

### 16. 下载文件

```bash
curl -X GET http://localhost:8080/api/upload/download/1 \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -o downloaded_file.jpg
```

### 17. 获取文件信息

```bash
curl http://localhost:8080/api/upload/1 \
  -H "Authorization: Bearer YOUR_TOKEN"
```

### 18. 获取我的所有文件列表

```bash
curl http://localhost:8080/api/upload/my \
  -H "Authorization: Bearer YOUR_TOKEN"
```

### 19. 根据类型获取我的文件列表

```bash
# 获取所有图片
curl "http://localhost:8080/api/upload/my/type?type=image" \
  -H "Authorization: Bearer YOUR_TOKEN"

# 获取所有视频
curl "http://localhost:8080/api/upload/my/type?type=video" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

### 20. 删除文件

```bash
curl -X DELETE http://localhost:8080/api/upload/1 \
  -H "Authorization: Bearer YOUR_TOKEN"
```

### 21. 统计我的文件数量

```bash
curl http://localhost:8080/api/upload/my/count \
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

### 2025-12-01
-  添加文件上传下载API（图片、视频、通用文件）
-  添加文件管理API（查询、删除、统计）
-  添加文件下载接口（带权限验证）
-  扩展ImageService支持多种文件类型
-  添加计划CRUD API（创建、查询、更新、删除）
- ✅ 添加计划列表API（我的计划、按日期范围查询）
- ✅ 添加计划统计API（统计计划数量）
- ✅ 计划功能支持日历时间选择

### 2025-11-10
- ✅ 添加用户认证API（登录、注册、登出等）
- ✅ 添加Token管理API（刷新、解析、验证）
- ✅ 添加笔记CRUD API
- ✅ 添加笔记列表API（我的、公开、用户）
- ✅ 创建Kotlin DSL API文档系统

---

## 📋 计划功能说明

### 计划状态
- `0`: 未完成（默认状态）
- `1`: 已完成
- `2`: 已取消

### 计划时间格式
计划时间使用 ISO 8601 格式：`YYYY-MM-DDTHH:mm:ss`

示例：
- `2025-11-17T10:00:00` - 2025年11月17日 10:00:00
- `2025-11-17T14:30:00` - 2025年11月17日 14:30:00

### 请求体示例

#### 创建计划请求
```json
{
    "title": "计划标题",
    "content": "计划内容（可选）",
    "planTime": "2025-11-17T10:00:00",
    "status": 0
}
```

#### 更新计划请求
```json
{
    "title": "更新的标题（可选）",
    "content": "更新的内容（可选）",
    "planTime": "2025-11-17T11:00:00",
    "status": 1
}
```

**注意**：更新时，所有字段都是可选的，只更新提供的字段。

---

## 📁 文件上传下载功能说明

### 支持的文件类型

#### 图片格式
- JPEG/JPG
- PNG
- GIF
- WEBP
- BMP

#### 视频格式
- MP4
- AVI
- MOV
- WMV
- FLV
- WEBM

#### 通用文件
- 支持所有文件类型（通过 `/api/upload/file` 接口）

### 文件大小限制
- 所有文件最大大小：**50MB**

### 文件存储路径
- 图片：`/uploads/images/`
- 视频：`/uploads/videos/`
- 通用文件：`/uploads/files/`

### 文件访问方式

#### 1. 直接访问（静态资源）
上传成功后，可以通过返回的 `url` 字段直接访问：
```
http://localhost:8080/uploads/images/abc123def456.jpg
http://localhost:8080/uploads/videos/xyz789.mp4
```

#### 2. 下载接口（带权限验证）
使用下载接口可以确保只有文件所有者才能下载：
```
GET /api/upload/download/{id}
```

### 请求示例

#### 上传图片（使用curl）
```bash
curl -X POST http://localhost:8080/api/upload/image \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -F "file=@/path/to/image.jpg"
```

#### 上传文件（使用JavaScript/Fetch）
```javascript
const formData = new FormData();
formData.append('file', fileInput.files[0]);

fetch('http://localhost:8080/api/upload/image', {
  method: 'POST',
  headers: {
    'Authorization': 'Bearer YOUR_TOKEN'
  },
  body: formData
})
.then(response => response.json())
.then(data => console.log(data));
```

#### 上传文件（使用axios）
```javascript
const formData = new FormData();
formData.append('file', fileInput.files[0]);

axios.post('http://localhost:8080/api/upload/image', formData, {
  headers: {
    'Authorization': 'Bearer YOUR_TOKEN',
    'Content-Type': 'multipart/form-data'
  }
})
.then(response => console.log(response.data));
```

### 响应格式

#### 上传成功响应
```json
{
    "code": 200,
    "message": "图片上传成功",
    "data": {
        "id": 1,
        "userId": 1,
        "url": "/uploads/images/abc123def456.jpg",
        "path": "/home/user/project/uploads/images/abc123def456.jpg",
        "size": 102400,
        "contentType": "image/jpeg",
        "createTime": "2025-11-16T15:30:00"
    }
}
```

#### 错误响应
```json
{
    "code": 500,
    "message": "不支持的文件类型：application/pdf",
    "data": null
}
```

### 文件管理

- **查看文件信息**：`GET /api/upload/{id}`
- **获取文件列表**：`GET /api/upload/my`
- **按类型筛选**：`GET /api/upload/my/type?type=image`
- **删除文件**：`DELETE /api/upload/{id}`（会同时删除物理文件和数据库记录）
- **统计文件数量**：`GET /api/upload/my/count`

---

**最后更新**: 2025-11-16  
**API版本**: v1.2  
**文档状态**: ✅ 完整

