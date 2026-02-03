package com.example.flagsentinelapi.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret:}")
    private String secretFromConfig;
    @Value("${jwt.expiration}")
    private long expiration;
    private Key key;

    @PostConstruct
    public void init() {
        if (secretFromConfig == null || secretFromConfig.isBlank()) {
            // Modo DEV: generar clave automáticamente
            key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
            System.out.println("⚠️ JWT SECRET generada automáticamente (DEV MODE)");
        } else {
            // Modo PROD o DEV con clave definida
            key = Keys.hmacShaKeyFor(secretFromConfig.getBytes());
        }
    }

    /*
     * Mirar como usar un GUID o correo para subject al crear el token no usar el username
     * ver tambien si la key para firmar es recomendable tenerla en un cert en cacert
     */
    public String generateToken(String username) {
        return Jwts.builder().setSubject(username).setExpiration(new Date(System.currentTimeMillis() + expiration)).signWith(key).compact();
    }

    public String extractUsername(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().getSubject();
    }

    public boolean isValid(String token) {
        try {
            extractUsername(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}

