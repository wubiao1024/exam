package com.exam.common;

import com.alibaba.fastjson.JSON;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.IOException;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Result<T> {
    private Integer code;
    private boolean success;
    private String message;
    @Schema(type = "any")
    private T data;

    public static <T> Result<T> success() {
        return new Result<>(2000, true, "成功", null);
    }

    public static <T> Result<T> success(String message, T data) {
        return new Result<>(2000, true, message, data);
    }

    public static <T> Result<T> success(T data) {
        return new Result<>(2000, true, "成功", data);
    }

    public static <T> Result<T> success(String message) {
        return new Result<>(2000, true, message, null);
    }

    public static <T> Result<T> fail() {
        return new Result<>(3000, false, "失败", null);
    }

    public static <T> Result<T> fail(Integer code, String message) {
        return new Result<>(code, false, message, null);
    }

    public static <T> Result<T> fail(Integer code) {
        return new Result<>(code, false, "失败", null);
    }

    public static <T> Result<T> fail(String message) {
        return new Result<>(3000, false, message, null);
    }

    public static <T> void output(HttpServletResponse response, Result<T> result) throws IOException {
        String res = JSON.toJSONString(result);
        response.setStatus(200);
        response.setCharacterEncoding("utf-8");
        response.setContentType("application/json");
        response.getWriter().print(res);
    }


}
