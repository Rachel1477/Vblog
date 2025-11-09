# Token 解析功能完成总结

## ✅ 已实现的功能

### 1. TokenInfo DTO 类
创建了 `TokenInfo` 类来封装 Token 中的所有信息：
- **uid**: 用户唯一标识符（Long）
- **username**: 用户名（String）
- **issuedAt**: Token 签发时间 / 登录时间（Date）
- **timestamp**: 签发时间戳（Long，毫秒）
- **expiresAt**: Token 过期时间（Date）

### 2. JwtUtil 新增方法

```java
// 获取 Token 签发时间（登录时间）
public Date getIssuedAtFromToken(String token)

// 获取 Token 签发时间戳（毫秒）
public Long getIssuedAtTimestamp(String token)

// 解析 Token，获取完整信息（推荐使用）
public TokenInfo parseToken(String token)
```

### 3. TokenHelper 工具类（推荐使用）

便捷的工具类，直接从 HttpServletRequest 中提取信息：

```java
// 提取 Token
public String extractToken(HttpServletRequest request)

// 获取 uid
public Long getUidFromRequest(HttpServletRequest request)

// 获取用户名
public String getUsernameFromRequest(HttpServletRequest request)

// 获取登录时间戳
public Long getLoginTimestampFromRequest(HttpServletRequest request)

// 解析完整 Token 信息（推荐）
public TokenInfo parseTokenFromRequest(HttpServletRequest request)

// 检查是否已认证
public boolean isAuthenticated(HttpServletRequest request)
```

### 4. API 接口

**POST /api/user/parse-token** - 解析 Token 获取信息

请求示例：
```bash
curl -X POST http://localhost:8080/api/user/parse-token \
  -H "Authorization: Bearer YOUR_TOKEN"
```

响应示例：
```json
{
    "code": 200,
    "message": "Token 解析成功",
    "data": {
        "uid": 1,
        "username": "admin",
        "issuedAt": "2025-11-09T15:52:32.000+00:00",
        "expiresAt": "2025-11-10T15:52:32.000+00:00",
        "timestamp": 1762703552000
    }
}
```

## 📖 使用方法

### 方法一：从请求属性获取（最简单，推荐用于获取 uid）

```java
@PostMapping("/create")
@RequireAuth
public Result<Object> create(HttpServletRequest request) {
    // 拦截器已经解析并存入属性
    Long uid = (Long) request.getAttribute("userId");
    String username = (String) request.getAttribute("username");
    
    // 如果需要登录时间，使用 TokenHelper
    Long loginTimestamp = tokenHelper.getLoginTimestampFromRequest(request);
    
    // 使用 uid 和 timestamp...
    return Result.success(data);
}
```

### 方法二：使用 TokenHelper（最推荐）

```java
@PostMapping("/create")
@RequireAuth
public Result<Object> create(HttpServletRequest request) {
    // 一次性获取所有信息
    TokenInfo tokenInfo = tokenHelper.parseTokenFromRequest(request);
    
    Long uid = tokenInfo.getUid();
    Long loginTimestamp = tokenInfo.getTimestamp();
    String username = tokenInfo.getUsername();
    
    // 使用这些信息...
    return Result.success(data);
}
```

### 方法三：直接使用 JwtUtil

```java
@PostMapping("/create")
@RequireAuth
public Result<Object> create(HttpServletRequest request) {
    // 提取 Token
    String token = request.getHeader("Authorization").substring(7);
    
    // 解析 Token
    TokenInfo tokenInfo = jwtUtil.parseToken(token);
    
    // 使用信息...
    return Result.success(data);
}
```

## 💡 典型使用场景

### 场景1：创建记录时记录创建者

```java
@PostMapping("/note/create")
@RequireAuth
public Result<Note> createNote(@RequestBody NoteRequest request, 
                               HttpServletRequest httpRequest) {
    TokenInfo tokenInfo = tokenHelper.parseTokenFromRequest(httpRequest);
    
    Note note = new Note();
    note.setUserId(tokenInfo.getUid());           // 创建者 uid
    note.setAuthor(tokenInfo.getUsername());      // 创建者用户名
    note.setContent(request.getContent());
    note.setCreateTime(new Date());
    note.setLoginTimestamp(tokenInfo.getTimestamp());  // 记录登录时间
    
    noteService.save(note);
    return Result.success(note);
}
```

### 场景2：权限验证

```java
@PutMapping("/note/{id}")
@RequireAuth
public Result<Note> updateNote(@PathVariable Long id,
                               @RequestBody NoteRequest request,
                               HttpServletRequest httpRequest) {
    // 获取当前用户 uid
    Long currentUid = tokenHelper.getUidFromRequest(httpRequest);
    
    // 查询笔记
    Note note = noteService.findById(id);
    
    // 验证权限：只能修改自己的笔记
    if (!note.getUserId().equals(currentUid)) {
        return Result.error(403, "无权限：只能修改自己的笔记");
    }
    
    // 更新笔记...
    return Result.success(note);
}
```

### 场景3：操作日志记录

```java
@PostMapping("/note/delete/{id}")
@RequireAuth
public Result<Void> deleteNote(@PathVariable Long id,
                               HttpServletRequest request) {
    TokenInfo tokenInfo = tokenHelper.parseTokenFromRequest(request);
    
    // 记录操作日志
    OperationLog log = new OperationLog();
    log.setUserId(tokenInfo.getUid());
    log.setUsername(tokenInfo.getUsername());
    log.setOperation("DELETE_NOTE");
    log.setNoteId(id);
    log.setOperateTime(new Date());
    log.setLoginTime(tokenInfo.getIssuedAt());
    log.setLoginTimestamp(tokenInfo.getTimestamp());
    
    logService.save(log);
    
    // 删除笔记...
    noteService.delete(id);
    
    return Result.success(null);
}
```

### 场景4：统计分析

```java
@GetMapping("/user/stats")
@RequireAuth
public Result<Map<String, Object>> getUserStats(HttpServletRequest request) {
    TokenInfo tokenInfo = tokenHelper.parseTokenFromRequest(request);
    
    // 计算登录时长
    long loginDuration = System.currentTimeMillis() - tokenInfo.getTimestamp();
    
    Map<String, Object> stats = new HashMap<>();
    stats.put("uid", tokenInfo.getUid());
    stats.put("username", tokenInfo.getUsername());
    stats.put("loginTime", tokenInfo.getIssuedAt());
    stats.put("loginDuration", loginDuration);
    stats.put("noteCount", noteService.countByUserId(tokenInfo.getUid()));
    
    return Result.success(stats);
}
```

## 🧪 测试结果

### 测试 1: 登录获取 Token ✅
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

### 测试 2: 解析 Token ✅
```json
{
    "code": 200,
    "message": "Token 解析成功",
    "data": {
        "uid": 1,
        "username": "admin",
        "issuedAt": "2025-11-09T15:52:32.000+00:00",
        "expiresAt": "2025-11-10T15:52:32.000+00:00",
        "timestamp": 1762703552000
    }
}
```

### 关键信息提取 ✅
- 📍 uid (用户ID): 1
- 👤 username: admin
- 🕐 timestamp (登录时间戳): 1762703552000
- 📅 issuedAt (登录时间): 2025-11-09T15:52:32.000+00:00
- ⏰ expiresAt (过期时间): 2025-11-10T15:52:32.000+00:00

## ✨ 核心优势

1. **安全性**: Token 中的信息（uid、timestamp）无法被客户端篡改
2. **便捷性**: 无需在每个接口中额外传递 uid 和 timestamp
3. **统一性**: 所有接口使用相同的方式获取用户信息
4. **可追溯**: 包含登录时间，便于审计和统计
5. **高性能**: 拦截器解析一次，后续直接从请求属性获取

## 📝 代码示例文件

已创建完整的示例代码：
- `ExampleController.java` - 包含6个使用场景的完整示例
- `Token解析使用说明.md` - 详细的使用文档

## 🔗 相关文件

- `src/main/java/org/example/dto/TokenInfo.java` - TokenInfo DTO
- `src/main/java/org/example/util/JwtUtil.java` - JWT 工具类
- `src/main/java/org/example/util/TokenHelper.java` - Token 辅助工具类
- `src/main/java/org/example/controller/UserController.java` - 解析接口
- `src/main/java/org/example/controller/ExampleController.java` - 使用示例

## 🎯 使用建议

### 推荐方式（性能最优）

```java
@PostMapping("/create")
@RequireAuth
public Result<Object> create(HttpServletRequest request) {
    // 1. 从请求属性获取 uid（拦截器已解析，性能最优）
    Long uid = (Long) request.getAttribute("userId");
    
    // 2. 需要登录时间时，使用 TokenHelper
    Long loginTimestamp = tokenHelper.getLoginTimestampFromRequest(request);
    
    // 3. 需要完整信息时，使用 TokenHelper 一次性获取
    // TokenInfo tokenInfo = tokenHelper.parseTokenFromRequest(request);
    
    return Result.success(data);
}
```

## 📌 注意事项

1. Token 必须有效才能解析
2. 建议在拦截器中解析一次，后续从请求属性获取（性能最优）
3. 异常情况需要捕获处理
4. uid 和 timestamp 都来自服务器签发的 Token，安全可靠

## ✅ 完成状态

- [x] 创建 TokenInfo DTO
- [x] 扩展 JwtUtil 工具类
- [x] 创建 TokenHelper 工具类
- [x] 添加 Token 解析 API 接口
- [x] 创建完整使用示例
- [x] 编写详细文档
- [x] 功能测试验证

所有功能已完成并测试通过！✨

