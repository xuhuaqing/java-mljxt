package com.example.springbootdemo.security;

import com.example.springbootdemo.common.UnauthorizedException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    private final TokenService tokenService;

    public AuthInterceptor(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }
        String uri = request.getRequestURI();
        if (!uri.startsWith("/api/admin/")) {
            return true;
        }

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new UnauthorizedException("未登录或Token缺失");
        }
        String token = authHeader.substring("Bearer ".length()).trim();
        TokenPayload payload = tokenService.verifyToken(token);
        CurrentUser currentUser = new CurrentUser(payload.userId(), payload.role());
        AuthContext.set(currentUser);

        RoleAllowed roleAllowed = handlerMethod.getMethodAnnotation(RoleAllowed.class);
        if (roleAllowed == null) {
            roleAllowed = handlerMethod.getBeanType().getAnnotation(RoleAllowed.class);
        }
        if (roleAllowed != null) {
            int role = payload.role() == null ? -1 : payload.role();
            boolean pass = Arrays.stream(roleAllowed.value()).anyMatch(v -> v == role);
            if (!pass) {
                throw new UnauthorizedException("无权限访问");
            }
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        AuthContext.clear();
    }
}
