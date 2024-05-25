package com.exam.filter;

import com.exam.common.Result;
import com.exam.config.RequestPathMatchRules;
import com.exam.entity.LoginUser;
import com.exam.utils.JwtUtils;
import com.exam.utils.RedisCache;
import jakarta.annotation.Resource;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.Objects;

// 请求只会经过过滤器一次
@Component
public class TokenAuthenticationFilter extends OncePerRequestFilter {
    @Resource
    RedisCache redisCache;

    @Override
    protected void doFilterInternal(jakarta.servlet.http.HttpServletRequest request, jakarta.servlet.http.HttpServletResponse response, jakarta.servlet.FilterChain filterChain) throws jakarta.servlet.ServletException, IOException {
        // 请求的路径
        String URI = request.getRequestURI();

        // inPermitAll
        if (RequestPathMatchRules.inPermitAll(URI)) {
            filterChain.doFilter(request, response);
            return;
        }
        // 获取token
        String token = request.getHeader("token");
        //inAnonymous
        if (RequestPathMatchRules.inAnonymous(URI)) {
            // 带token
            if (StringUtils.hasText(token)) {
                Result.output(response, Result.fail("访问的资源不能携带token"));
                return;
            }
            // 不带token
            filterChain.doFilter(request, response);
            return;
        }
        // inAuthenticated
        // 没有携带token
        if (!StringUtils.hasText(token)) {
            Result.output(response, Result.fail("请先登录"));
            return;
        }
        // 解析token
        Long id = JwtUtils.getIdByToken(token);
        // 从redis 获取用户信息
        LoginUser user = redisCache.getCacheObject("login:" + id.toString());
        if (id == -1 || Objects.isNull(user)) {
            Result.output(response, Result.fail("登录信息失效"));
            return;
        }
        // 认证成功
        // TODO 获取权限信息封装到 Authentication
        // 第三个参数可以标识当前用户为已经认证的状态
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user, null,
                user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        filterChain.doFilter(request, response);
    }


}
