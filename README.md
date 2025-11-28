# Vblog - 个人笔记系统

基于 Spring Boot + Vue 3 的全栈个人笔记系统。

## 技术栈

### 后端
- **框架**: Spring Boot 3.2.0
- **持久层**: MyBatis 3.0.3
- **数据库**: MySQL 8.0
- **缓存**: Redis (用于 Token 管理)
- **认证**: JWT (JJWT 0.12.3)
- **密码加密**: SHA-256
- **构建工具**: Maven
- **容器化**: Docker & Docker Compose

### 前端
- **框架**: Vue 3.5.22
- **构建工具**: Vite 7.1.11
- **路由**: Vue Router 4.6.3
- **状态管理**: Pinia 3.0.3
- **HTTP 客户端**: Axios 1.13.2

## 核心功能

### 1. 用户认证系统
- 用户注册（用户名、密码、邮箱、昵称）
-  用户登录（SHA-256 密码加密）
-  修改密码
-  用户信息查询

### 2. JWT 身份验证
-  JWT Token 生成与验证
- Token 自动刷新
-  Token 失效机制：
  - 24小时过期
  - 24小时未活动自动失效
  - 用户登出手动失效
  - 修改密码后失效
-  Redis 存储 Token 和活动时间
-  拦截器自动验证请求

### 3. 权限控制
-  `@RequireAuth` 注解：标记需要认证的接口
-  `@GuestAllowed` 注解：标记游客可访问的接口
-  游客模式：未登录用户可查看公开内容，但不能修改

## 项目结构

```
Vblog/
├── sql/                          # 数据库初始化脚本
│   └── init.sql
├── src/main/                     # 后端源码
│   ├── java/org/example/
│   │   ├── VblogApplication.java          # 主应用类
│   │   ├── annotation/                    # 自定义注解
│   │   │   ├── RequireAuth.java          # 需要认证注解
│   │   │   └── GuestAllowed.java         # 游客可访问注解
│   │   ├── common/                        # 通用类
│   │   │   └── Result.java               # 统一响应结果
│   │   ├── config/                        # 配置类
│   │   │   ├── RedisConfig.java          # Redis 配置
│   │   │   └── WebConfig.java            # Web 配置（拦截器、CORS）
│   │   ├── controller/                    # 控制器
│   │   │   ├── UserController.java       # 用户相关接口
│   │   │   ├── NoteController.java       # 笔记相关接口
│   │   │   └── TestController.java       # 测试接口
│   │   ├── dto/                           # 数据传输对象
│   │   ├── entity/                        # 实体类
│   │   ├── interceptor/                   # 拦截器
│   │   ├── mapper/                        # MyBatis Mapper
│   │   ├── service/                       # 服务层
│   │   └── util/                          # 工具类
│   └── resources/
│       ├── application.properties         # 应用配置
│       ├── mapper/                        # MyBatis XML
│       └── static/                        # 静态资源
├── vue-vblog/                    # 前端项目（Vue 3 + Vite）
│   ├── src/
│   │   ├── api/                           # API 请求封装
│   │   ├── components/                    # Vue 组件
│   │   ├── router/                        # 路由配置
│   │   ├── store/                         # Pinia 状态管理
│   │   ├── views/                         # 页面视图
│   │   ├── App.vue                        # 根组件
│   │   └── main.js                        # 入口文件
│   ├── package.json                       # 前端依赖配置
│   └── vite.config.js                     # Vite 配置
├── docker-compose.yaml                    # Docker 编排文件
├── pom.xml                               # Maven 配置
├── API文档.md                            # API 接口文档
├── JWT文档.md                            # JWT 认证文档
└── README.md                             # 项目说明
```

## 快速开始

### 1. 环境要求

- **后端**:
  - Java 21
  - Maven 3.6+
  - Docker & Docker Compose
- **前端**:
  - Node.js 20.19.0+ 或 22.12.0+
  - npm 或 yarn

### 2. 启动数据库

```bash
cd /home/rachel/Desktop/Vblog
docker-compose up -d
```

这会启动：
- MySQL 8.0 (端口: 10000)
- Redis (端口: 6379)

等待数据库初始化完成（约10-30秒），可以通过以下命令检查：

```bash
docker logs my-mysql
```

### 3. 启动后端应用

```bash
# 在项目根目录
mvn spring-boot:run
```

后端应用将在 `http://localhost:8080` 启动。

### 4. 启动前端应用

打开新的终端窗口，执行：

```bash
# 进入前端目录
cd /home/rachel/Desktop/Vblog/vue-vblog

# 安装依赖（首次运行需要）
npm install

# 启动开发服务器
npm run dev
```

前端应用将在 `http://localhost:5173` 启动（Vite 默认端口）。

### 5. 访问应用

- **前端界面**: http://localhost:5173
- **后端 API**: http://localhost:8080
- **后端测试页面**:
  - 基础测试: http://localhost:8080/test.html
  - 登录测试: http://localhost:8080/login-test.html
  - JWT 测试: http://localhost:8080/jwt-test.html

## API 接口

### 用户认证（无需 Token）

| 接口 | 方法 | 地址 | 说明 |
|------|------|------|------|
| 登录 | POST | `/api/user/login` | 返回 JWT Token |
| 注册 | POST | `/api/user/register` | 创建新用户 |
| 查询用户 | GET | `/api/user/{username}` | 游客可访问 |

### Token 管理（需要 Token）

| 接口 | 方法 | 地址 | 说明 |
|------|------|------|------|
| 登出 | POST | `/api/user/logout` | 使 Token 失效 |
| 刷新 Token | POST | `/api/user/refresh-token` | 获取新 Token |
| 检查 Token | GET | `/api/user/check-token` | 验证 Token 状态 |
| 当前用户 | GET | `/api/user/current` | 获取登录用户信息 |

### 用户操作（需要 Token）

| 接口 | 方法 | 地址 | 说明 |
|------|------|------|------|
| 修改密码 | POST | `/api/user/change-password` | 修改后 Token 失效 |

详细 API 文档请查看：[API文档.md](API文档.md)

## JWT 认证流程

```
┌─────────────┐
│   登录请求   │
└──────┬──────┘
       │
       ▼
┌─────────────┐
│ 验证用户名密码│
└──────┬──────┘
       │
       ▼
┌─────────────┐
│  生成 Token  │
│ (包含用户信息)│
└──────┬──────┘
       │
       ▼
┌─────────────┐
│存储到 Redis  │
│记录活动时间   │
└──────┬──────┘
       │
       ▼
┌─────────────┐
│ 返回给前端   │
└─────────────┘

后续请求：
┌─────────────┐
│带 Token 请求 │
└──────┬──────┘
       │
       ▼
┌─────────────┐
│JWT 拦截器验证│
│1.格式检查    │
│2.签名验证    │
│3.过期检查    │
│4.Redis 检查  │
│5.活动时间检查│
└──────┬──────┘
       │
       ├─✅ 通过 ──> 更新活动时间 ──> 执行业务逻辑
       │
       └─❌ 失败 ──> 返回 401
```

详细 JWT 文档请查看：[JWT文档.md](JWT文档.md)

## 数据库结构

### user 表

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

### 预设账号

- **用户名**: admin
- **密码**: 123456
- **邮箱**: admin@example.com

## 配置说明

### application.properties

```properties
# 服务器配置
server.port=8080

# 数据库配置
spring.datasource.url=jdbc:mysql://localhost:10000/vblog
spring.datasource.username=root
spring.datasource.password=123456

# Redis 配置
spring.data.redis.host=localhost
spring.data.redis.port=6379

# JWT 配置
jwt.secret=VblogSecretKeyForJWTTokenGenerationAndValidation2025
jwt.expiration=86400000  # 24小时
```

## 测试示例

### 1. 登录获取 Token

```bash
curl -X POST http://localhost:8080/api/user/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"123456"}'
```

### 2. 使用 Token 访问受保护接口

```bash
TOKEN="your_jwt_token_here"

curl -X GET http://localhost:8080/api/user/current \
  -H "Authorization: Bearer $TOKEN"
```

### 3. 刷新 Token

```bash
curl -X POST http://localhost:8080/api/user/refresh-token \
  -H "Authorization: Bearer $TOKEN"
```

### 4. 登出

```bash
curl -X POST http://localhost:8080/api/user/logout \
  -H "Authorization: Bearer $TOKEN"
```

## 安全特性

### 密码安全
-  SHA-256 加密存储
-  密码长度验证（至少6位）
-  修改密码后自动失效旧 Token

### Token 安全
-  JWT 签名验证（HS384）
- Token 有效期控制（24小时）
-  24小时未活动自动失效
-  Redis 集中管理，支持主动失效
-  HTTPS 传输（生产环境建议）

### API 安全
-  CORS 跨域配置
- 拦截器统一验证
-  注解式权限控制
-  游客模式限制写权限

## 开发指南

### 添加需要认证的接口

```java
@RestController
@RequestMapping("/api/note")
public class NoteController {
    
    // 需要登录才能访问
    @PostMapping("/create")
    @RequireAuth
    public Result<Note> createNote(@RequestBody NoteRequest request) {
        // 可以通过 HttpServletRequest 获取当前用户
        // String username = (String) request.getAttribute("username");
        // Long userId = (Long) request.getAttribute("userId");
        // ...
    }
    
    // 游客也可以访问
    @GetMapping("/{id}")
    @GuestAllowed
    public Result<Note> getNote(@PathVariable Long id) {
        // ...
    }
}
```

### 在服务中获取当前用户

```java
@Service
public class NoteService {
    
    @Autowired
    private HttpServletRequest request;
    
    public Note createNote(String content) {
        // 从请求中获取当前用户信息
        String username = (String) request.getAttribute("username");
        Long userId = (Long) request.getAttribute("userId");
        
        // 创建笔记...
    }
}
```

## 故障排查

### 问题1: Token 验证失败
**检查**:
1. Token 格式是否正确（Bearer token）
2. Token 是否已过期
3. Redis 是否正常运行
4. 是否24小时未活动

### 问题2: 跨域错误
**解决**: WebConfig 中已配置 CORS，确保没有在 Controller 上使用 `@CrossOrigin`

### 问题3: 无法连接数据库
**检查**:
```bash
docker ps | grep mysql
docker logs my-mysql
```

### 问题4: Redis 连接失败
**检查**:
```bash
docker ps | grep redis
docker exec -it my-redis redis-cli ping
```

## 后续开发计划

- ~~[ ] 笔记 CRUD 功能~~
- [ ] 笔记分类和标签
- [ ] 笔记搜索功能
- [ ] 文件上传（图片、附件）
- [ ] 笔记分享功能
- [ ] 评论功能
- [ ] 用户头像上传
- [ ] 用户资料编辑
- [ ] 邮箱验证
- [ ] 找回密码功能
- [ ] 操作日志记录
- [ ] 数据统计分析

## 文档

- [API 接口文档](API目录.md)
- [JWT 认证文档](JWT文档.md)

## 许可证

MIT License

## 联系方式

如有问题或建议，请提交 Issue。

