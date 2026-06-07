package pl.edu.ur.teachly.common.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

/**
 * Serwis odpowiedzialny za generowanie i weryfikację tokenów JWT.
 *
 * <p>Tokeny są podpisywane algorytmem HMAC-SHA przy użyciu klucza skonfigurowanego w {@code
 * application.security.jwt.secret-key}.
 */
@Service
public class JwtService {

    @Value("${application.security.jwt.secret-key}")
    private String secretKey;

    @Value("${application.security.jwt.expiration}")
    private long jwtExpiration;

    /**
     * Wyodrębnia nazwę użytkownika (subject) z tokenu JWT.
     *
     * @param token token JWT
     * @return nazwa użytkownika zawarta w tokenie
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Wyodrębnia dowolne roszczenie (claim) z tokenu JWT przy użyciu podanej funkcji.
     *
     * @param token token JWT
     * @param claimsResolver funkcja mapująca obiekt Claims na oczekiwany typ
     * @param <T> typ zwracanej wartości
     * @return wartość roszczenia
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Generuje token JWT dla podanego użytkownika bez dodatkowych roszczeń.
     *
     * @param userDetails dane uwierzytelniające użytkownika
     * @return podpisany token JWT
     */
    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    /**
     * Generuje token JWT dla podanego użytkownika z dodatkowymi roszczeniami.
     *
     * @param extraClaims dodatkowe pary klucz-wartość umieszczane w payloadzie tokenu
     * @param userDetails dane uwierzytelniające użytkownika
     * @return podpisany token JWT
     */
    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return buildToken(extraClaims, userDetails, jwtExpiration);
    }

    /**
     * Sprawdza, czy token jest ważny dla danego użytkownika. Token jest uznawany za ważny, gdy
     * zawiera poprawną nazwę użytkownika i nie wygasł.
     *
     * @param token token JWT
     * @param userDetails dane uwierzytelniające użytkownika
     * @return {@code true} jeśli token jest ważny
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private String buildToken(
            Map<String, Object> extraClaims, UserDetails userDetails, long expiration) {
        return Jwts.builder()
                .claims(extraClaims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey())
                .compact();
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
