package com.roman.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.sql.Date;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
@Service
public class JWTUtil {

    private static final String SECRET_KEY = "foo_bar_123_foo_bar_123_foo_bar_123_foo_bar_123_foo_bar_123_foo_bar_123";

    public String issueToken(String subject){
        return issueToken(subject, Map.of());
    }

    public String issueToken(String subject, String ...scopes){
        return issueToken(subject, Map.of("scopes", scopes));
    }

    public String issueToken(String subject, List<String> scopes){
        return issueToken(subject, Map.of("scopes", scopes));
    }
    public String issueToken(String subject, Map<String, Object> claims){
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuer("roka")
                .setIssuedAt(Date.from(Instant.now()))
                .setExpiration(Date.from(Instant.now().plus(15, ChronoUnit.DAYS)))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String getSubject(String token){
        return getClaims(token).getSubject();
    }

    public boolean isTokenValid(String jwt, String username){
        String subject = getSubject(jwt);
        return subject.equals(username) && !isTokenExpired(jwt);
    }

    public boolean isTokenExpired(String jwt){
        return getClaims(jwt).getExpiration().before(Date.from(Instant.now()));
    }

    private Claims getClaims(String token){
        return Jwts
                .parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSigningKey(){
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }
}
