package com.exam.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.UUID;


public class JwtUtils {

    //默认有效期为7天
    private static final long defaultTime = 1000 * 60 * 60 * 24 * 7;
    //签名
    private static final String signature = "adhjdjkasdnnjksahdjbbbii-sdjnndhhiioo00088dd";
    private static final SecretKey KEY = Keys.hmacShaKeyFor(signature.getBytes());

    /**
     * 创建 token
     *
     * @param id      用户id
     * @param message 需要加密的额外信息
     * @param time    过期时间
     * @return 通过jwt生成的 token
     */
    public static String createToken(Long id, String message, long time) {
        /*JwtBuilder jwtBuilder = Jwts.builder();
        String token = jwtBuilder
                //header
                .setHeaderParam("type", "JWT")
                .setHeaderParam("alg", "HS256")
                //payloada载荷
                .claim("id", id)
                .claim("message", message)
                .setSubject("all-user") // 所有用户
                .setExpiration(new Date(System.currentTimeMillis() + time))
                .setId(UUID.randomUUID().toString())
                //KEY签名
                .signWith(SignatureAlgorithm.HS256, KEY)
                .compact();
        return token;*/


        return Jwts.builder()
                // HEADER
                .header()
                .add("type", "JWT")
                .add("alg", "HS256")
                // payload
                .and()
                .claim("id", id)
                .claim("message", message)
                .id(UUID.randomUUID().toString())
                .expiration(new Date(System.currentTimeMillis() + time))
                .signWith(KEY, Jwts.SIG.HS256)
                .compact();

    }

    //创建用户的token
    public static String createToken(Long id) {
        return createToken(id, null, defaultTime);
    }

    public static String createToken(Long id, Long time) {
        return createToken(id, null, time);
    }

    public static String createToken(Long id, String message) {
        return createToken(id, message, defaultTime);
    }


    /**
     * 检查token是否合法
     *
     * @param token 请求得到的token
     * @return false 不合法 , true 合法
     */
    public static Boolean checkToken(String token) {
        if (token == null) {
            return false;
        }
        try {
            Jwts.parser()
                    .verifyWith(KEY)
                    .build()
                    .parseSignedClaims(token);
        } catch (Exception e) {
            return false;
        }
        return true;
    }


    /**
     * 公用的方法
     *
     * @param token token
     * @param type  可能的取值的 message 或者 id
     * @return 需要的信息
     */
    private static Object getInfoByToken(String token, String type) {
        Jws<Claims> claimsJws;
        if (token == null) {
            return null;
        }
        try {
            claimsJws = Jwts.parser()
                    .verifyWith(KEY)
                    .build()
                    .parseSignedClaims(token);
        } catch (Exception e) {
            System.out.println("json解析失败");
            return -1;
        }
        Claims payload = claimsJws.getPayload();
        return payload.get(type);
    }

    /**
     * 根据token获取用户id
     *
     * @param token token
     * @return 用户id，返回-1代表token解析失败
     */
    public static Long getIdByToken(String token) {
        return Long.valueOf((Integer) getInfoByToken(token, "id"));
    }

    /**
     * 根据token获取加密的额外信息
     *
     * @param token token
     * @return message 额外的加密信息 返回-1代表token解析失败
     */
    public static String getMessageByToken(String token) {
        return (String) getInfoByToken(token, "message");
    }
}
