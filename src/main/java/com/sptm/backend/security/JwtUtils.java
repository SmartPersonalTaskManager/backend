package com.sptm.backend.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtils {

    @Value("${sptm.app.jwtSecret:SecretKeyToGenJWTsMustBeLongerThan256bitsForHS512Algorithm}")
    private String jwtSecret;

    @Value("${sptm.app.jwtExpirationMs:86400000}")
    private int jwtExpirationMs;

    public String generateJwtToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(key(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Key key() {
        // In production, use standard Base64 decoding of a secure secret
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(
                "SecretKeyToGenJWTsMustBeLongerThan256bitsForHS512AlgorithmSecretKeyToGenJWTsMustBeLongerThan256bitsForHS512Algorithm"));
    }

    public String getUserNameFromJwtToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key()).build()
                .parseClaimsJws(token).getBody().getSubject();
    }

    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parserBuilder().setSigningKey(key()).build().parse(authToken);
            return true;
        } catch (MalformedJwtException | ExpiredJwtException | UnsupportedJwtException | IllegalArgumentException e) {
            // Log issues
        }
        return false;
    }
}
