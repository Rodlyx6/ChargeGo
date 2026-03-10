package com.example.gateway.filter;

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
 * 拦截所有请求，白名单直接放行，业务接口校验 Token
 */
@Component
public class AuthFilter implements GlobalFilter, Ordered {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AntPathMatcher pathMatcher;

    // 白名单：这些接口不需要登录就能访问
    private static final List<String> WHITE_LIST = List.of(
            "/user/register",
            "/user/login"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getPath().value();

        // 1. 白名单直接放行
        for (String whitePath : WHITE_LIST) {
            if (pathMatcher.match(whitePath, path)) {
                return chain.filter(exchange);
            }
        }

        // 2. 从请求头中获取 Token
        String token = request.getHeaders().getFirst("Authorization");
        if (token == null || token.isBlank()) {
            return writeErrorResponse(exchange.getResponse(), HttpStatus.UNAUTHORIZED, "请先登录");
        }

        // 3. 去掉 Bearer 前缀（如果有）
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        // 4. 解析并校验 Token
        Claims claims;
        try {
            claims = JwtUtil.parseToken(token);
        } catch (Exception e) {
            return writeErrorResponse(exchange.getResponse(), HttpStatus.UNAUTHORIZED, "Token 无效或已过期");
        }

        // 5. 管理员接口权限校验：路径包含 /admin 的必须是管理员（role=1）
        Integer role = claims.get("role", Integer.class);
        if (path.contains("/admin") && !Integer.valueOf(1).equals(role)) {
            return writeErrorResponse(exchange.getResponse(), HttpStatus.FORBIDDEN, "无权限访问");
        }

        // 6. 将 userId 和 role 写入请求头，转发给下游服务
        Long userId = claims.get("userId", Long.class);
        ServerHttpRequest mutatedRequest = request.mutate()
                .header("X-User-Id", String.valueOf(userId))
                .header("X-User-Role", String.valueOf(role))
                .build();

        return chain.filter(exchange.mutate().request(mutatedRequest).build());
    }

    /**
     * 返回 JSON 格式的错误响应
     */
    private Mono<Void> writeErrorResponse(ServerHttpResponse response, HttpStatus status, String message) {
        response.setStatusCode(status);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> errorBody = Map.of(
                "code", status.value(),
                "msg", message,
                "data", null
        );

        String json;
        try {
            json = objectMapper.writeValueAsString(errorBody);
        } catch (Exception e) {
            json = "{\"code\":500,\"msg\":\"服务器内部错误\",\"data\":null}";
        }

        DataBuffer buffer = response.bufferFactory().wrap(json.getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(buffer));
    }

    @Override
    public int getOrder() {
        // 数字越小优先级越高，-100 保证这个过滤器最先执行
        return -100;
    }
}
