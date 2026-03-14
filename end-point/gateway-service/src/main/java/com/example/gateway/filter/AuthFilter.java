package com.example.gateway.filter;

import com.example.common.constant.UserRole;
import com.example.common.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * 全局 JWT 鉴权过滤器
 */
@Component
public class AuthFilter implements GlobalFilter, Ordered {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AntPathMatcher pathMatcher;

    private static final List<String> WHITE_LIST = List.of(
            "/user/register",
            "/user/login",
            "/admin/login"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getPath().value();

        // 白名单直接放行
        if (isWhitePath(path)) {
            return chain.filter(exchange);
        }

        // 获取并验证 token
        String token = extractToken(request);
        if (token == null) {
            return unauthorized(exchange.getResponse(), "请先登录");
        }

        // 解析 token
        Claims claims = parseToken(token);
        if (claims == null) {
            return unauthorized(exchange.getResponse(), "Token 无效或已过期");
        }

        // 管理员权限检查
        Long userId = claims.get("userId", Long.class);
        Integer role = claims.get("role", Integer.class);
        
        if (path.contains("/admin") && !UserRole.ADMIN.equals(role)) {
            return forbidden(exchange.getResponse(), "您没有管理员权限");
        }

        // 添加用户信息到请求头
        ServerHttpRequest newRequest = request.mutate()
                .header("X-User-Id", String.valueOf(userId))
                .build();

        return chain.filter(exchange.mutate().request(newRequest).build());
    }

    /**
     * 检查是否白名单路径
     */
    private boolean isWhitePath(String path) {
        return WHITE_LIST.stream().anyMatch(white -> pathMatcher.match(white, path));
    }

    /**
     * 提取 token
     */
    private String extractToken(ServerHttpRequest request) {
        String authHeader = request.getHeaders().getFirst("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }

    /**
     * 解析 token
     */
    private Claims parseToken(String token) {
        try {
            return JwtUtil.parseToken(token);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 返回 401 未授权
     */
    private Mono<Void> unauthorized(ServerHttpResponse response, String message) {
        return writeErrorResponse(response, HttpStatus.UNAUTHORIZED, message);
    }

    /**
     * 返回 403 禁止访问
     */
    private Mono<Void> forbidden(ServerHttpResponse response, String message) {
        return writeErrorResponse(response, HttpStatus.FORBIDDEN, message);
    }

    /**
     * 写入错误响应
     */
    private Mono<Void> writeErrorResponse(ServerHttpResponse response, HttpStatus status, String message) {
        response.setStatusCode(status);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> errorBody = Map.of(
                "code", status.value(),
                "msg", message,
                "data", ""
        );

        String json;
        try {
            json = objectMapper.writeValueAsString(errorBody);
        } catch (Exception e) {
            json = "{\"code\":500,\"msg\":\"系统内部错误\",\"data\":\"\"}";
        }

        DataBuffer buffer = response.bufferFactory().wrap(json.getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(buffer));
    }

    @Override
    public int getOrder() {
        return -100;
    }
}
