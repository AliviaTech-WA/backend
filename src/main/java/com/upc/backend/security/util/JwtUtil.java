package com.upc.backend.security.util;

import com.upc.backend.security.entities.UsuarioPrincipal;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.function.Function;

@Component
// Infraestructura común de HU04: genera, interpreta y valida los JWT de acceso.
public class JwtUtil {

    private final SecretKey key;
    private final long expirationMs;

    public JwtUtil(@Value("${app.jwt.secret:}") String configuredSecret,
                   @Value("${app.jwt.expiration-ms:1200000}") long expirationMs) {
        this.key = buildKey(configuredSecret);
        this.expirationMs = expirationMs;
    }

    public String generateToken(UsuarioPrincipal principal) {
        Date ahora = new Date();
        return Jwts.builder()
                .setSubject(principal.getUsername())
                .claim("usuarioId", principal.getId())
                .claim("rol", principal.getRol())
                .setIssuedAt(ahora)
                .setExpiration(new Date(ahora.getTime() + expirationMs))
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        return extractUsername(token).equalsIgnoreCase(userDetails.getUsername())
                && extractClaim(token, Claims::getExpiration).after(new Date());
    }

    private <T> T extractClaim(String token, Function<Claims, T> resolver) {
        Claims claims = Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(token).getBody();
        return resolver.apply(claims);
    }

    private SecretKey buildKey(String configuredSecret) {
        if (configuredSecret == null || configuredSecret.isBlank()) {
            return Keys.secretKeyFor(SignatureAlgorithm.HS512);
        }
        byte[] source;
        try {
            source = Decoders.BASE64.decode(configuredSecret);
        } catch (RuntimeException ex) {
            source = configuredSecret.getBytes(StandardCharsets.UTF_8);
        }
        try {
            return Keys.hmacShaKeyFor(MessageDigest.getInstance("SHA-512").digest(source));
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("SHA-512 no está disponible.", ex);
        }
    }
}
