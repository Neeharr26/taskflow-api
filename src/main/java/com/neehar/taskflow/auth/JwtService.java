package com.neehar.taskflow.auth;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import javax.crypto.SecretKey;
import java.util.Date;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {
private final SecretKey signingKey;
private final long expirationMs;

public JwtService(
		@Value("${app.jwt.secret}") String secret,
		@Value("${app.jwt.expiration-ms}") long expirationMs) {
	this.signingKey=Keys.hmacShaKeyFor(secret.getBytes());
	this.expirationMs = expirationMs;
}
public String generateToken(String email) {
	Date now=new Date();
	Date expiry=new Date(now.getTime()+expirationMs);
	 return Jwts.builder()
             .subject(email)
             .issuedAt(now)
             .expiration(expiry)
             .signWith(signingKey)
             .compact();
}
public String extractEmail(String token) {
    return parseClaims(token).getSubject();
}
public boolean isTokenValid(String token) {
    try {
        parseClaims(token);
        return true;
    } catch (Exception e) {
        return false;
    }
}
private Claims parseClaims(String token) {
    return Jwts.parser()
            .verifyWith(signingKey)
            .build()
            .parseSignedClaims(token)
            .getPayload();
}
}
