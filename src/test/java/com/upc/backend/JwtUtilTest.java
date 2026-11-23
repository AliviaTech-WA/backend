package com.upc.backend;

import com.upc.backend.entities.Usuario;
import com.upc.backend.security.entities.UsuarioPrincipal;
import com.upc.backend.security.util.JwtUtil;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    @Test
    void conservaFirmaHs512ClaimsYValidacionConSecretoConfigurado() throws Exception {
        var usuario = new Usuario();
        usuario.setId(42L);
        usuario.setCorreo("paciente@aliviatech.test");
        usuario.setRol("PACIENTE");
        var principal = new UsuarioPrincipal(usuario);
        String secreto = "not-base64!shared-secret";
        var emisor = new JwtUtil(secreto, 1_200_000);
        var validador = new JwtUtil(secreto, 1_200_000);

        String token = emisor.generateToken(principal);

        var key = Keys.hmacShaKeyFor(MessageDigest.getInstance("SHA-512")
                .digest(secreto.getBytes(StandardCharsets.UTF_8)));
        var firmado = Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
        assertEquals("HS512", firmado.getHeader().getAlgorithm());
        assertEquals("paciente@aliviatech.test", validador.extractUsername(token));
        assertTrue(validador.validateToken(token, principal));
        var claims = firmado.getPayload();
        assertEquals(42, ((Number) claims.get("usuarioId")).intValue());
        assertEquals("PACIENTE", claims.get("rol"));
        assertNotNull(claims.getIssuedAt());
        assertNotNull(claims.getExpiration());
    }
}
