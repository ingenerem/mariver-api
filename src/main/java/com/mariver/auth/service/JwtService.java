package com.mariver.auth.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

@Service
public class JwtService {

    /*
     * Secret key used to sign and verify JWTs.
     *
     * IMPORTANT:
     * - This should come from application.yml or environment variables.
     * - Never hardcode real secrets in production.
     */
    @Value("${jwt.secret}")
    private String jwtSecret;

    /*
     * Token expiration time in milliseconds.
     *
     * Example:
     * 1000 * 60 * 60 * 24
     * = 24 hours
     */
    @Value("${jwt.expiration}")
    private long jwtExpiration;

    /*
     * Converts the raw secret string into a cryptographic signing key.
     *
     * JWT signing requires a Key object, not a plain String.
     */
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    /*
     * Generates a JWT token for a user.
     *
     * FLOW:
     * 1. User logs in successfully.
     * 2. AuthService calls generateToken(email).
     * 3. JWT is created and signed.
     * 4. Token is returned to frontend.
     * 5. Frontend stores token.
     * 6. Frontend sends token on future requests.
     */
    public String generateToken(String email) {

        return Jwts.builder()

                /*
                 * subject = main identity stored inside token
                 */
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))

                //Digitally signs the token.
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /*
     * Extracts the email from a JWT.
     */
    public String extractEmail(String token) {
        return extractClaims(token).getSubject();
    }

    /*
     * Validates whether token belongs to expected user
     * AND is not expired.
     */
    public boolean isTokenValid(String token, String email) {

        String extractedEmail = extractEmail(token);

        return extractedEmail.equals(email)
                && !isTokenExpired(token);
    }

    /*
     * Checks whether token expiration date has passed.
     */
    private boolean isTokenExpired(String token) {
        return extractClaims(token)
                .getExpiration()
                .before(new Date());
    }

    /*
     * Parses JWT and extracts all claims (payload data).
     *
     * Claims include:
     * - subject
     * - issuedAt
     * - expiration
     * - custom fields if added later
     *
     * Signature validation also happens here automatically.
     */
    private Claims extractClaims(String token) {

        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
