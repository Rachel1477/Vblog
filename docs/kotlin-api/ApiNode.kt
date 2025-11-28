package org.example.api

/**
 * API节点基类
 * 用于构建树形API结构
 */
open class ApiNode(
    val path: String,
    val method: HttpMethod = HttpMethod.GET,
    val description: String = "",
    val requireAuth: Boolean = false,
    val guestAllowed: Boolean = false
) {
    private val children = mutableListOf<ApiNode>()
    
    /**
     * 添加子节点
     */
    fun add(node: ApiNode): ApiNode {
        children.add(node)
        return node
    }
    
    /**
     * 获取完整路径
     */
    fun fullPath(): String = path
    
    /**
     * 获取所有子节点
     */
    fun getChildren(): List<ApiNode> = children.toList()
    
    /**
     * 生成文档字符串
     */
    fun toDoc(indent: Int = 0): String {
        val prefix = "  ".repeat(indent)
        val authInfo = when {
            requireAuth -> "[需要认证]"
            guestAllowed -> "[游客可访问]"
            else -> ""
        }
        
        val doc = StringBuilder()
        doc.append("$prefix- ${method.name} $path $authInfo $description\n")
        
        children.forEach { child ->
            doc.append(child.toDoc(indent + 1))
        }
        
        return doc.toString()
    }
    
    override fun toString(): String = "$method $path - $description"
}

    /**
 * HTTP方法枚举
 */
enum class HttpMethod {
    GET, POST, PUT, DELETE, PATCH
}

/**
 * 创建GET请求节点
 */
fun get(path: String, description: String = "", requireAuth: Boolean = false, guestAllowed: Boolean = false) =
    ApiNode(path, HttpMethod.GET, description, requireAuth, guestAllowed)

    /**
 * 创建POST请求节点
     */
fun post(path: String, description: String = "", requireAuth: Boolean = false, guestAllowed: Boolean = false) =
    ApiNode(path, HttpMethod.POST, description, requireAuth, guestAllowed)

/**
 * 创建PUT请求节点
 */
fun put(path: String, description: String = "", requireAuth: Boolean = false, guestAllowed: Boolean = false) =
    ApiNode(path, HttpMethod.PUT, description, requireAuth, guestAllowed)

/**
 * 创建DELETE请求节点
 */
fun delete(path: String, description: String = "", requireAuth: Boolean = false, guestAllowed: Boolean = false) =
    ApiNode(path, HttpMethod.DELETE, description, requireAuth, guestAllowed)
