/*
package com.exam.interceptor;
import com.exam.common.vo.Result;
import com.exam.repository.ManagerRepository;
import com.exam.mapper.UserMapper;
import com.exam.utils.DbCheckTokenUtils;
import com.exam.utils.JwtUtils;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.@Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class TokenInterceptoe implements HandlerInterceptor {
    @@Autowired
    UserMapper userMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //获取头文件中的token
        String token = request.getHeader("token");
        //设置编码方式
        response.setContentType("application/json; charset=utf-8");
        response.setCharacterEncoding("utf-8");
        Gson gson = new Gson();
        //默认不合法
        String resultJson = gson.toJson(Result.fail("身份校验失败！"));
        if(token==null){
            resultJson  = gson.toJson(Result.fail("身份校验失败！"));
        }
        //能JWT能解密
        if(JwtUtils.checkToken(token)){
            String role = JwtUtils.getRoleByToken(token);
            if (role.equals("user") && DbCheckTokenUtils.userCheckByToken(token)){
                return true;

            }
        } else {//无法解密,第一关都过不去!
            resultJson  = gson.toJson(Result.fail("身份校验失败！"));
        }
        response.getWriter().println(resultJson);
        return false;
    }
}
*/
