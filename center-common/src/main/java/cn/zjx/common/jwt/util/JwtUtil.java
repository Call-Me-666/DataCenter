package cn.zjx.common.jwt.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import jdk.jfr.Description;
import org.intellij.lang.annotations.MagicConstant;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description:
 * @Program: data-center
 * @Author: zjx
 * @Create: 2022-07-18 16:07:47
 * @Version: 1.0
 **/
public class JwtUtil {
    final static String flag = "token&1998";    // 转码标识
    public static final int DAY = 5;
    public static final int HOUR = 11;
    public static final int MINUTE = 12;
    public static final int SECOND = 13;

    /**
     * 生成token
     *
     * @param claims  生成token的参数
     * @param field 超时时间参数类型
     * @param outTime 设置超时时间
     * @return
     */
    public String generateToken(Map<String, String> claims, @MagicConstant(intValues = {DAY, HOUR, MINUTE, SECOND}) int field, int outTime) {
        JWTCreator.Builder builder = JWT.create();
        claims.forEach((p, t) -> {
            builder.withClaim(p, t);
        });

        builder.withSubject("user");
        Calendar calendar = Calendar.getInstance();
        builder.withIssuedAt(calendar.getTime());
        calendar.add(field, outTime);
        builder.withExpiresAt(calendar.getTime());
        String token = builder.sign(Algorithm.HMAC256(flag));
        return token;
    }

    /**
     * 解析token
     *
     * @param token
     * @return
     */
    public DecodedJWT decodeToken(String token) {
        DecodedJWT decodedJWT = null;
        try {
            decodedJWT = JWT.require(Algorithm.HMAC256(flag)).build().verify(token);
        } catch (Exception e) {
            throw e;
        }finally {
            return decodedJWT;
        }
    }
}
