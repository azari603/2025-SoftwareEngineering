package com.cheack.softwareengineering.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Collections;
import java.util.Date;

@Slf4j
@Component
public class JwtProvider {

    /** 기존 키 우선: 4.jwt.secret → 없으면 app.jwt.secret */
    @Value("${4.jwt.secret:${app.jwt.secret}}")
    private String secret;

    @Value("${app.jwt.issuer:CHEACK}")
    private String issuer;

    /** 기존 ms 단위 */
    @Value("${jwt.access-expiration:0}")
    private long accessExpMs;

    @Value("${jwt.refresh-expiration:0}")
    private long refreshExpMs;

    /** 대안: 초 단위(없으면 0) */
    @Value("${app.jwt.access-exp-seconds:0}")
    private long accessExpSecondsProp;

    @Value("${app.jwt.refresh-exp-seconds:0}")
    private long refreshExpSecondsProp;

    private SecretKey secretKey;
    private long accessExpSeconds;
    private long refreshExpSeconds;

    @PostConstruct
    void init() {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        // 우선순위: 초 단위 프로퍼티 > ms 단위 프로퍼티
        this.accessExpSeconds  = accessExpSecondsProp  > 0 ? accessExpSecondsProp  : Math.max(1, accessExpMs  / 1000);
        this.refreshExpSeconds = refreshExpSecondsProp > 0 ? refreshExpSecondsProp : Math.max(1, refreshExpMs / 1000);
        log.info("JWT exp(access={}s, refresh={}s)", accessExpSeconds, refreshExpSeconds);
    }

    /** ---- 메서드 이름: 기존 핸들러 호환(create*) + 신규(generate*) 둘 다 제공 ---- */
    public String createAccessToken(String username)  { return buildToken(username, "access", accessExpSeconds); }
    public String createRefreshToken(String username) { return buildToken(username, "refresh", refreshExpSeconds); }
    public String generateAccessToken(String username)  { return createAccessToken(username); }
    public String generateRefreshToken(String username) { return createRefreshToken(username); }

    private String buildToken(String username, String typ, long expSeconds) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(username)
                .issuer(issuer)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(expSeconds)))
                .claim("typ", typ)
                .signWith(secretKey, Jwts.SIG.HS256)
                .compact();
    }

    public boolean validate(String token) {
        try {
            Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            log.debug("JWT validate fail: {}", e.getMessage());
            return false;
        }
    }

    public String extractUsername(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().getSubject();
    }

    public boolean isAccessToken(String token) {
        Object typ = Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("typ");
        return "access".equals(String.valueOf(typ));
    }

    public boolean isRefreshToken(String token) {
        Object typ = Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("typ");
        return "refresh".equals(String.valueOf(typ));
    }

    public Authentication getAuthentication(String token) {
        String username = extractUsername(token);
        return new UsernamePasswordAuthenticationToken(username, null, Collections.emptyList());
    }

    public long getAccessExpSeconds() {
        return accessExpSeconds;
    }

    public long getRefreshExpSeconds() {
        return refreshExpSeconds;
    }
}