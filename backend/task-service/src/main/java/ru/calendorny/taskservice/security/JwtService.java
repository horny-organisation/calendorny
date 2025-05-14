package ru.calendorny.taskservice.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

    private final JwtProperties jwtProperties;
    private final PublicKey publicKey;
    private final JwtParser parser;

    public JwtService(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
        this.publicKey = loadPublicKey(jwtProperties.publicKey());
        this.parser = Jwts.parser().verifyWith(publicKey).build();
    }

    public Claims extractAllClaims(String token) {
        Jws<Claims> jws = parser.parseSignedClaims(token);
        return jws.getPayload();
    }

    public boolean isTokenValid(String token) {
        try {
            parser.parseSignedClaims(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    private PublicKey loadPublicKey(String publicKey) {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(publicKey);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
            return KeyFactory.getInstance("RSA").generatePublic(keySpec);
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to load public key", e);
        }
    }
}
