package com.example.springbootdemo.security;

import com.example.springbootdemo.common.UnauthorizedException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

@Service
public class TokenService {

    private static final String HMAC_SHA256 = "HmacSHA256";
    private static final Base64.Encoder URL_ENCODER = Base64.getUrlEncoder().withoutPadding();
    private static final Base64.Decoder URL_DECODER = Base64.getUrlDecoder();

    private final String secret;
    private final long expireSeconds;

    public TokenService(
            @Value("${app.auth.token-secret:mljxt-dev-secret-change-me}") String secret,
            @Value("${app.auth.token-expire-seconds:86400}") long expireSeconds
    ) {
        this.secret = secret;
        this.expireSeconds = expireSeconds;
    }

    public String issueToken(Long userId, Integer role) {
        String header = URL_ENCODER.encodeToString("HS256".getBytes(StandardCharsets.UTF_8));
        long exp = System.currentTimeMillis() / 1000 + expireSeconds;
        String payloadRaw = userId + ":" + role + ":" + exp;
        String payload = URL_ENCODER.encodeToString(payloadRaw.getBytes(StandardCharsets.UTF_8));
        String signInput = header + "." + payload;
        String signature = URL_ENCODER.encodeToString(hmacSha256(signInput));
        return signInput + "." + signature;
    }

    public TokenPayload verifyToken(String token) {
        try {
            String[] arr = token.split("\\.");
            if (arr.length != 3) {
                throw new UnauthorizedException("Token格式错误");
            }
            String signInput = arr[0] + "." + arr[1];
            byte[] expect = hmacSha256(signInput);
            byte[] actual = URL_DECODER.decode(arr[2]);
            if (!MessageDigest.isEqual(expect, actual)) {
                throw new UnauthorizedException("Token校验失败");
            }
            String payloadRaw = new String(URL_DECODER.decode(arr[1]), StandardCharsets.UTF_8);
            String[] parts = payloadRaw.split(":");
            if (parts.length != 3) {
                throw new UnauthorizedException("Token载荷错误");
            }
            TokenPayload payload = new TokenPayload(
                    Long.parseLong(parts[0]),
                    Integer.parseInt(parts[1]),
                    Long.parseLong(parts[2])
            );
            long now = System.currentTimeMillis() / 1000;
            if (payload.exp() < now) {
                throw new UnauthorizedException("Token已过期");
            }
            return payload;
        } catch (UnauthorizedException e) {
            throw e;
        } catch (Exception e) {
            throw new UnauthorizedException("Token无效");
        }
    }

    private byte[] hmacSha256(String input) {
        try {
            Mac mac = Mac.getInstance(HMAC_SHA256);
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), HMAC_SHA256));
            return mac.doFinal(input.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new IllegalStateException("Token签名失败");
        }
    }
}
