package com.auth.security;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import com.auth.model.ROLE;
import io.jsonwebtoken.*;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {
	@Value("${jwt.secret}")
    private String SECRET;

	public String generateToken(String username, ROLE role) {
        return Jwts.builder().setSubject(username)
        		.claim("role", role.name())
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + 86400000))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }
	
	private Key getSignKey() {
		return Keys.hmacShaKeyFor(SECRET.getBytes());
	}
}
