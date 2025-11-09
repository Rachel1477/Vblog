package org.example.api

/**
 * Vblog API 定义
 * 使用 Kotlin DSL 实现 API 自文档化
 * 
 * 所有的API接口都在这里定义，便于管理和查阅
 */
object VblogAPI {
    
    private const val BASE_URL = "http://localhost:8080"
    
    /**
     * 用户相关 API
     */
    object User {
        private const val BASE = "/api/user"
        
        /**
         * 用户登录（获取JWT Token）
         * POST /api/user/login
         * 游客可访问
         */
        val Login = post(
            path = "$BASE/login",
            description = "用户登录，返回JWT Token",
            guestAllowed = true
        )
        
        /**
         * 用户注册
         * POST /api/user/register
         * 游客可访问
         */
        val Register = post(
            path = "$BASE/register",
            description = "用户注册",
            guestAllowed = true
        )
        
        /**
         * 用户登出
         * POST /api/user/logout
         * 需要认证
         */
        val Logout = post(
            path = "$BASE/logout",
            description = "用户登出，使Token失效",
            requireAuth = true
        )
        
        /**
         * 刷新Token
         * POST /api/user/refresh-token
         * 需要认证
         */
        val RefreshToken = post(
            path = "$BASE/refresh-token",
            description = "刷新Token，获取新Token",
            requireAuth = true
        )
        
        /**
         * 检查Token状态
         * GET /api/user/check-token
         * 需要认证
         */
        val CheckToken = get(
            path = "$BASE/check-token",
            description = "检查Token是否有效",
            requireAuth = true
        )
        
        /**
         * 解析Token获取用户信息
         * POST /api/user/parse-token
         * 需要认证
         */
        val ParseToken = post(
            path = "$BASE/parse-token",
            description = "解析Token，获取uid和登录时间",
            requireAuth = true
        )
        
        /**
         * 获取当前登录用户信息
         * GET /api/user/current
         * 需要认证
         */
        val GetCurrent = get(
            path = "$BASE/current",
            description = "获取当前登录用户信息",
            requireAuth = true
        )
        
        /**
         * 根据用户名查询用户信息
         * GET /api/user/{username}
         * 游客可访问
         */
        fun GetByUsername(username: String) = get(
            path = "$BASE/$username",
            description = "根据用户名查询用户信息",
            guestAllowed = true
        )
        
        /**
         * 修改密码
         * POST /api/user/change-password
         * 需要认证
         */
        val ChangePassword = post(
            path = "$BASE/change-password",
            description = "修改密码（修改后Token失效）",
            requireAuth = true
        )
        
        /**
         * 获取完整的API文档
         */
        fun doc(): String {
            return """
                |=== 用户 API ===
                |${Login}
                |${Register}
                |${Logout}
                |${RefreshToken}
                |${CheckToken}
                |${ParseToken}
                |${GetCurrent}
                |${GetByUsername("{username}")}
                |${ChangePassword}
            """.trimMargin()
        }
    }
    
    /**
     * 笔记相关 API
     */
    object Note {
        private const val BASE = "/api/note"
        
        /**
         * 创建笔记
         * POST /api/note/create
         * 需要认证
         */
        val Create = post(
            path = "$BASE/create",
            description = "创建笔记",
            requireAuth = true
        )
        
        /**
         * 获取笔记详情
         * GET /api/note/{id}
         * 游客可访问（公开笔记）
         */
        fun GetById(id: Long) = get(
            path = "$BASE/$id",
            description = "获取笔记详情（公开笔记游客可访问）",
            guestAllowed = true
        )
        
        /**
         * 更新笔记
         * PUT /api/note/{id}
         * 需要认证
         */
        fun Update(id: Long) = put(
            path = "$BASE/$id",
            description = "更新笔记（只能修改自己的笔记）",
            requireAuth = true
        )
        
        /**
         * 删除笔记
         * DELETE /api/note/{id}
         * 需要认证
         */
        fun Delete(id: Long) = delete(
            path = "$BASE/$id",
            description = "删除笔记（只能删除自己的笔记）",
            requireAuth = true
        )
        
        /**
         * 获取我的笔记列表
         * GET /api/note/my
         * 需要认证
         */
        val GetMyNotes = get(
            path = "$BASE/my",
            description = "获取我的笔记列表",
            requireAuth = true
        )
        
        /**
         * 获取指定用户的笔记列表
         * GET /api/note/user/{userId}
         * 游客可访问
         */
        fun GetUserNotes(userId: Long) = get(
            path = "$BASE/user/$userId",
            description = "获取指定用户的笔记列表",
            guestAllowed = true
        )
        
        /**
         * 获取所有公开笔记
         * GET /api/note/public
         * 游客可访问
         */
        val GetPublicNotes = get(
            path = "$BASE/public",
            description = "获取所有公开笔记",
            guestAllowed = true
        )
        
        /**
         * 统计用户笔记数量
         * GET /api/note/count/{userId}
         * 游客可访问
         */
        fun CountUserNotes(userId: Long) = get(
            path = "$BASE/count/$userId",
            description = "统计用户笔记数量",
            guestAllowed = true
        )
        
        /**
         * 获取完整的API文档
         */
        fun doc(): String {
            return """
                |=== 笔记 API ===
                |${Create}
                |${GetById(1)}
                |${Update(1)}
                |${Delete(1)}
                |${GetMyNotes}
                |${GetUserNotes(1)}
                |${GetPublicNotes}
                |${CountUserNotes(1)}
            """.trimMargin()
        }
    }
    
    /**
     * 测试相关 API
     */
    object Test {
        private const val BASE = "/api"
        
        /**
         * GET测试
         * GET /api/test
         */
        val Get = get(
            path = "$BASE/test",
            description = "GET请求测试",
            guestAllowed = true
        )
        
        /**
         * POST测试
         * POST /api/test
         */
        val Post = post(
            path = "$BASE/test",
            description = "POST请求测试",
            guestAllowed = true
        )
        
        /**
         * 获取完整的API文档
         */
        fun doc(): String {
            return """
                |=== 测试 API ===
                |${Get}
                |${Post}
            """.trimMargin()
        }
    }
    
    /**
     * 生成完整的API文档
     */
    fun generateDoc(): String {
        return """
            |╔═══════════════════════════════════════════════════════════╗
            |║           Vblog API 文档 (自动生成)                        ║
            |║           Base URL: $BASE_URL                             ║
            |╚═══════════════════════════════════════════════════════════╝
            |
            |${User.doc()}
            |
            |${Note.doc()}
            |
            |${Test.doc()}
            |
            |说明：
            |  [需要认证] - 需要在请求头添加: Authorization: Bearer {token}
            |  [游客可访问] - 无需Token即可访问
            |
        """.trimMargin()
    }
    
    /**
     * 打印所有API
     */
    fun printAll() {
        println(generateDoc())
    }
}

/**
 * 主函数 - 用于生成和打印API文档
 */
fun main() {
    VblogAPI.printAll()
}

