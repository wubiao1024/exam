package com.exam.config;

import lombok.Getter;

import java.util.Arrays;
import java.util.stream.Stream;

public class RequestPathMatchRules {
    // api 文档相关的路径，一次给他放行
    private static final String[] swagger = {
            "/swagger-ui/**",
            "/swagger-ui/",
            "/swagger-ui",
            "/favicon.ico",
            "/v2/api-docs",
            "/v3/api-docs",
            "/v3/api-docs/**",
            "/v2/api-docs/**",
            "/swagger-ui.html",
            "/swagger-ui/index.html",
            "/swagger-resources/**",
            "/webjars/**",
            "/doc.html",

    };
    // 没有登录才能访问的api
    @Getter
    private static final String[] anonymous = {};
    @Getter
    private static final String[] permitAllPath = {"/auth/**","/api/user/login"};
    //无论是否登录都可以访问的api
    @Getter
    private static final String[] permitAll = Stream.concat(Arrays.stream(permitAllPath), Arrays.stream(swagger)).toArray(String[]::new);

    /**
     * 判断某一个路径是否是 无论是否登录都可以访问的api  anonymous
     *
     * @param path 判断的路径
     * @return true 包含 , false 没有包含
     */
    public static boolean inAnonymous(String path) {
        return inPatterns(anonymous, path);
    }

    /**
     * 判断某一个路径是否是 无论是否登录都可以访问的api  permitAll
     *
     * @param path 判断的路径
     * @return true 包含 , false 没有包含
     */
    public static boolean inPermitAll(String path) {
        return inPatterns(permitAll, path);
    }

    /**
     * 判断某一个路径是否是 需要严格校验的api    authenticated
     *
     * @param path 判断的路径
     * @return true 包含 , false 没有包含
     */
    public static boolean inAuthenticated(String path) {
        return !inAnonymous(path) && !inPermitAll(path);
    }

    public static boolean inPatterns(String[] patterns, String path) {
        path = removeTrailingSlash(path); // 去除路径末尾的斜杠
        for (String pattern : patterns) {
            pattern = removeTrailingSlash(pattern); // 去除模式末尾的斜杠
            if (pattern.endsWith("/**")) { // 如果模式以 "/**" 结尾
                String prefix = pattern.substring(0, pattern.length() - 3); // 截取前缀部分
                if (path.startsWith(prefix)) { // 检查给定路径是否以前缀开头
                    return true;
                }
            } else {
                if (path.equals(pattern)) { // 检查给定路径是否与模式完全匹配
                    return true;
                }
            }
        }
        return false;
    }

    // 去除路径末尾的斜杠
    private static String removeTrailingSlash(String path) {
        if (path.endsWith("/")) {
            return path.substring(0, path.length() - 1);
        }
        return path;
    }

}
