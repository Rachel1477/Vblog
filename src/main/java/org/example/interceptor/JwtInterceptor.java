package org.example.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.annotation.GuestAllowed;
import org.example.annotation.RequireAuth;
import org.example.service.TokenService;
import org.example.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * JWT 拦截器
 * 验证请求中的 Token 是否有效
 */
@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private TokenService tokenService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 如果不是方法处理器，直接放行
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;

        // 检查方法或类上是否有 @GuestAllowed 注解
        GuestAllowed guestAllowed = handlerMethod.getMethodAnnotation(GuestAllowed.class);
        if (guestAllowed == null) {
            guestAllowed = handlerMethod.getBeanType().getAnnotation(GuestAllowed.class);
        }

        // 如果允许游客访问，直接放行
        if (guestAllowed != null) {
            return true;
        }

        // 检查是否需要认证
        RequireAuth requireAuth = handlerMethod.getMethodAnnotation(RequireAuth.class);
        if (requireAuth == null) {
            requireAuth = handlerMethod.getBeanType().getAnnotation(RequireAuth.class);
        }

        // 如果不需要认证，直接放行
        if (requireAuth == null || !requireAuth.required()) {
            return true;
        }

        // 需要认证，验证 Token
        String token = getTokenFromRequest(request);
        
        if (token == null || token.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":401,\"message\":\"未登录，请先登录\",\"data\":null}");
            return false;
        }

        try {
            // 验证 Token 格式是否正确
            if (!jwtUtil.validateToken(token)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"code\":401,\"message\":\"Token 无效\",\"data\":null}");
                return false;
            }

            // 检查 Token 是否过期
            if (jwtUtil.isTokenExpired(token)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"code\":401,\"message\":\"Token 已过期，请重新登录\",\"data\":null}");
                return false;
            }

            // 从 Token 中获取用户信息
            String username = jwtUtil.getUsernameFromToken(token);
            Long userId = jwtUtil.getUserIdFromToken(token);

            // 检查 Token 是否在 Redis 中有效（包括24小时未活动检查）
            if (!tokenService.isTokenValid(username, token)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"code\":401,\"message\":\"登录已失效，请重新登录\",\"data\":null}");
                return false;
            }

            // 更新最后活动时间
            tokenService.updateLastActivity(username);

            // 将用户信息存入 request，供后续使用
            request.setAttribute("userId", userId);
            request.setAttribute("username", username);

            return true;

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":401,\"message\":\"认证失败：" + e.getMessage() + "\",\"data\":null}");
            return false;
        }
    }

    /**
     * 从请求头中获取 Token
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}

