package org.librarymanagement.utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    public String generateLoginToken(String username) {
        long expirationMillis = 1000 * 60 * 60 * 24;
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationMillis))
                .signWith(key)
                .compact();
    }

    public String generateVerificationToken(String email){
        long expirationMillis = 300000;
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMillis))
                .signWith(key)
                .compact();
    }

    public boolean validateToken(String token){
        return !isTokenExpired(token);
    }

    public String extractEmail(String token){
        JwtParserBuilder jwtParserBuilder = Jwts.parser(); // parser() này từ đoạn bạn đưa
        jwtParserBuilder.setSigningKey(key);

        return jwtParserBuilder.build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    private boolean isTokenExpired(String token){
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token){
        JwtParserBuilder jwtParserBuilder = Jwts.parser(); // parser() này từ đoạn bạn đưa
        jwtParserBuilder.setSigningKey(key);

        return jwtParserBuilder.build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
    }
}
