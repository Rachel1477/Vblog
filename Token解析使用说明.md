# Token 解析使用说明

## 概述

系统提供了完整的 Token 解析功能，可以从 JWT Token 中提取以下信息：
- **uid**: 用户唯一标识符（用户ID）
- **username**: 用户名
- **issuedAt**: Token 签发时间（登录时间）
- **timestamp**: 签发时间戳（毫秒）
- **expiresAt**: Token 过期时间

## TokenInfo 对象

```java
public class TokenInfo {
    private Long uid;              // 用户唯一标识符
    private String username;       // 用户名
    private Date issuedAt;         // Token 签发时间（登录时间）
    private Date expiresAt;        // Token 过期时间
    private Long timestamp;        // 签发时间戳（毫秒）
}
```

## 使用方法

### 1. 在 Controller 中使用

#### 方法一：通过 JwtUtil 直接解析

```java
@RestController
@RequestMapping("/api/note")
public class NoteController {
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @PostMapping("/create")
    @RequireAuth
    public Result<Note> createNote(@RequestBody NoteRequest request, 
                                   HttpServletRequest httpRequest) {
        // 从请求头获取 Token
        String token = getTokenFromRequest(httpRequest);
        
        // 解析 Token 获取完整信息
        TokenInfo tokenInfo = jwtUtil.parseToken(token);
        
        // 获取 uid 和登录时间
        Long uid = tokenInfo.getUid();
        Long loginTimestamp = tokenInfo.getTimestamp();
        
        // 使用 uid 和时间信息
        Note note = noteService.createNote(uid, loginTimestamp, request);
        
        return Result.success(note);
    }
    
    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
```

#### 方法二：通过 TokenHelper 工具类（推荐）

```java
@RestController
@RequestMapping("/api/note")
public class NoteController {
    
    @Autowired
    private TokenHelper tokenHelper;
    
    @PostMapping("/create")
    @RequireAuth
    public Result<Note> createNote(@RequestBody NoteRequest request, 
                                   HttpServletRequest httpRequest) {
        // 方式1：分别获取
        Long uid = tokenHelper.getUidFromRequest(httpRequest);
        Long loginTimestamp = tokenHelper.getLoginTimestampFromRequest(httpRequest);
        
        // 方式2：一次性获取所有信息
        TokenInfo tokenInfo = tokenHelper.parseTokenFromRequest(httpRequest);
        Long uid2 = tokenInfo.getUid();
        Long timestamp2 = tokenInfo.getTimestamp();
        
        // 使用 uid 和时间信息
        Note note = noteService.createNote(uid, loginTimestamp, request);
        
        return Result.success(note);
    }
}
```

#### 方法三：从请求属性获取（最简单）

拦截器已经将用户信息存入请求属性，可以直接获取：

```java
@PostMapping("/create")
@RequireAuth
public Result<Note> createNote(@RequestBody NoteRequest request, 
                               HttpServletRequest httpRequest) {
    // 拦截器已经解析并存入属性
    Long uid = (Long) httpRequest.getAttribute("userId");
    String username = (String) httpRequest.getAttribute("username");
    
    // 如果需要登录时间，使用 TokenHelper
    Long loginTimestamp = tokenHelper.getLoginTimestampFromRequest(httpRequest);
    
    // 使用 uid 和时间信息
    Note note = noteService.createNote(uid, loginTimestamp, request);
    
    return Result.success(note);
}
```

### 2. 在 Service 中使用

```java
@Service
public class NoteService {
    
    @Autowired
    private HttpServletRequest request;
    
    @Autowired
    private TokenHelper tokenHelper;
    
    public Note createNote(NoteRequest noteRequest) {
        // 获取当前用户的 uid
        Long uid = tokenHelper.getUidFromRequest(request);
        
        // 获取登录时间戳
        Long loginTimestamp = tokenHelper.getLoginTimestampFromRequest(request);
        
        // 或者一次性获取所有信息
        TokenInfo tokenInfo = tokenHelper.parseTokenFromRequest(request);
        
        Note note = new Note();
        note.setUserId(uid);
        note.setCreateTime(new Date());
        note.setContent(noteRequest.getContent());
        // ... 保存笔记
        
        return note;
    }
}
```

### 3. JwtUtil 提供的所有方法

```java
// 1. 获取用户ID（uid）
Long uid = jwtUtil.getUserIdFromToken(token);

// 2. 获取用户名
String username = jwtUtil.getUsernameFromToken(token);

// 3. 获取 Token 签发时间（Date 对象）
Date issuedAt = jwtUtil.getIssuedAtFromToken(token);

// 4. 获取 Token 签发时间戳（Long 毫秒）
Long timestamp = jwtUtil.getIssuedAtTimestamp(token);

// 5. 获取 Token 过期时间
Date expiresAt = jwtUtil.getExpirationDateFromToken(token);

// 6. 解析 Token 获取完整信息（推荐）
TokenInfo tokenInfo = jwtUtil.parseToken(token);
```

### 4. TokenHelper 提供的所有方法

```java
// 1. 从请求中提取 Token
String token = tokenHelper.extractToken(request);

// 2. 从请求中获取 uid
Long uid = tokenHelper.getUidFromRequest(request);

// 3. 从请求中获取用户名
String username = tokenHelper.getUsernameFromRequest(request);

// 4. 从请求中获取登录时间戳
Long loginTimestamp = tokenHelper.getLoginTimestampFromRequest(request);

// 5. 从请求中解析完整 Token 信息（推荐）
TokenInfo tokenInfo = tokenHelper.parseTokenFromRequest(request);

// 6. 检查是否已认证
boolean isAuth = tokenHelper.isAuthenticated(request);
```

## API 接口

### 解析 Token 接口

**接口**: `POST /api/user/parse-token`

**权限**: 需要认证（@RequireAuth）

**请求头**:
```
Authorization: Bearer {token}
```

**响应示例**:
```json
{
    "code": 200,
    "message": "Token 解析成功",
    "data": {
        "uid": 1,
        "username": "admin",
        "issuedAt": "2025-11-09T10:12:28.000+00:00",
        "expiresAt": "2025-11-10T10:12:28.000+00:00",
        "timestamp": 1731148348000
    }
}
```

**测试命令**:
```bash
curl -X POST http://localhost:8080/api/user/parse-token \
  -H "Authorization: Bearer YOUR_TOKEN"
```

## 使用场景示例

### 场景1：创建笔记时记录用户和时间

```java
@PostMapping("/create")
@RequireAuth
public Result<Note> createNote(@RequestBody NoteRequest request, 
                               HttpServletRequest httpRequest) {
    TokenInfo tokenInfo = tokenHelper.parseTokenFromRequest(httpRequest);
    
    Note note = new Note();
    note.setUserId(tokenInfo.getUid());              // 使用 uid
    note.setAuthor(tokenInfo.getUsername());         // 使用用户名
    note.setCreateTime(new Date());                  // 当前时间
    note.setLoginTime(tokenInfo.getIssuedAt());      // 登录时间
    note.setTimestamp(tokenInfo.getTimestamp());     // 时间戳
    note.setContent(request.getContent());
    
    noteService.save(note);
    return Result.success(note);
}
```

### 场景2：日志记录

```java
@PostMapping("/delete/{id}")
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
    noteService.delete(id);
    
    return Result.success(null);
}
```

### 场景3：权限验证

```java
@PutMapping("/update/{id}")
@RequireAuth
public Result<Note> updateNote(@PathVariable Long id, 
                               @RequestBody NoteRequest request,
                               HttpServletRequest httpRequest) {
    // 获取当前用户 uid
    Long currentUid = tokenHelper.getUidFromRequest(httpRequest);
    
    // 获取笔记
    Note note = noteService.findById(id);
    
    // 验证是否是笔记所有者
    if (!note.getUserId().equals(currentUid)) {
        return Result.error(403, "只能修改自己的笔记");
    }
    
    // 更新笔记
    note.setContent(request.getContent());
    note.setUpdateTime(new Date());
    noteService.update(note);
    
    return Result.success(note);
}
```

### 场景4：统计分析

```java
@GetMapping("/stats")
@RequireAuth
public Result<Map<String, Object>> getStats(HttpServletRequest request) {
    TokenInfo tokenInfo = tokenHelper.parseTokenFromRequest(request);
    
    // 计算用户本次登录时长
    long loginDuration = System.currentTimeMillis() - tokenInfo.getTimestamp();
    
    Map<String, Object> stats = new HashMap<>();
    stats.put("uid", tokenInfo.getUid());
    stats.put("username", tokenInfo.getUsername());
    stats.put("loginTime", tokenInfo.getIssuedAt());
    stats.put("loginDuration", loginDuration);  // 登录时长（毫秒）
    stats.put("noteCount", noteService.countByUserId(tokenInfo.getUid()));
    
    return Result.success(stats);
}
```

## 前端使用示例

### JavaScript 示例

```javascript
// 解析 Token 获取用户信息
async function parseToken() {
    const token = localStorage.getItem('jwt_token');
    
    const response = await axios.post('/api/user/parse-token', {}, {
        headers: {
            'Authorization': 'Bearer ' + token
        }
    });
    
    const tokenInfo = response.data.data;
    console.log('用户ID (uid):', tokenInfo.uid);
    console.log('用户名:', tokenInfo.username);
    console.log('登录时间:', tokenInfo.issuedAt);
    console.log('登录时间戳:', tokenInfo.timestamp);
    console.log('过期时间:', tokenInfo.expiresAt);
    
    return tokenInfo;
}

// 在创建笔记时使用
async function createNote(content) {
    const tokenInfo = await parseToken();
    
    const response = await axios.post('/api/note/create', {
        content: content,
        uid: tokenInfo.uid,           // 传递 uid
        timestamp: tokenInfo.timestamp // 传递时间戳
    }, {
        headers: {
            'Authorization': 'Bearer ' + localStorage.getItem('jwt_token')
        }
    });
    
    return response.data;
}
```

## 注意事项

1. **Token 必须有效**: 只能解析有效的、未过期的 Token
2. **异常处理**: Token 解析可能抛出异常，需要捕获处理
3. **性能考虑**: Token 解析有一定开销，建议在拦截器中解析一次，后续从请求属性获取
4. **安全性**: uid 和时间信息都来自 Token，无法被客户端篡改
5. **时间同步**: 使用服务器时间，避免客户端时间不准确的问题

## 最佳实践

### 推荐方式（性能最优）

```java
@PostMapping("/create")
@RequireAuth
public Result<Note> createNote(@RequestBody NoteRequest request, 
                               HttpServletRequest httpRequest) {
    // 1. 优先从请求属性获取（拦截器已解析）
    Long uid = (Long) httpRequest.getAttribute("userId");
    String username = (String) httpRequest.getAttribute("username");
    
    // 2. 需要时间信息时，使用 TokenHelper
    Long loginTimestamp = tokenHelper.getLoginTimestampFromRequest(httpRequest);
    
    // 3. 使用信息
    Note note = noteService.createNote(uid, username, loginTimestamp, request);
    
    return Result.success(note);
}
```

### 完整示例

```java
@Service
public class NoteService {
    
    @Autowired
    private TokenHelper tokenHelper;
    
    @Autowired
    private HttpServletRequest request;
    
    public Note createNote(NoteRequest noteRequest) {
        // 解析 Token 获取所有信息
        TokenInfo tokenInfo = tokenHelper.parseTokenFromRequest(request);
        
        if (tokenInfo == null) {
            throw new RuntimeException("未登录或 Token 无效");
        }
        
        Note note = new Note();
        note.setUserId(tokenInfo.getUid());
        note.setAuthor(tokenInfo.getUsername());
        note.setContent(noteRequest.getContent());
        note.setCreateTime(new Date());
        note.setLoginTimestamp(tokenInfo.getTimestamp());
        
        // 保存笔记
        noteMapper.insert(note);
        
        // 记录日志
        logOperation(tokenInfo, "CREATE_NOTE", note.getId());
        
        return note;
    }
    
    private void logOperation(TokenInfo tokenInfo, String operation, Long noteId) {
        OperationLog log = new OperationLog();
        log.setUserId(tokenInfo.getUid());
        log.setUsername(tokenInfo.getUsername());
        log.setOperation(operation);
        log.setNoteId(noteId);
        log.setOperateTime(new Date());
        log.setLoginTime(tokenInfo.getIssuedAt());
        logMapper.insert(log);
    }
}
```

## 总结

Token 解析功能提供了便捷的方式获取用户身份信息（uid）和登录时间，避免了在每个接口中传递这些信息。主要优势：

- ✅ **安全性**: Token 中的信息无法被篡改
- ✅ **便捷性**: 一次解析获取所有信息
- ✅ **统一性**: 所有接口使用相同的方式获取用户信息
- ✅ **可追溯**: 包含登录时间，便于审计和统计

