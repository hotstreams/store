package hotstreams.orderservice.security;

import hotstreams.orderservice.exceptions.InvalidTokenException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class JwtTokenValidator {
    @Value("${auth.jwt.secret}")
    private String jwtSecret;

    public boolean validate(final String token) {
        try {
            Jwts.parserBuilder().setSigningKey(jwtSecret.getBytes()).build().parseClaimsJws(token);
        } catch (SignatureException ex) {
            log.error("Invalid jwt signature");
            throw new InvalidTokenException("Invalid signature");
        } catch (MalformedJwtException ex) {
            log.error("Invalid jwt");
            throw new InvalidTokenException("Malformed jwt");
        } catch (ExpiredJwtException ex) {
            log.error("Expired jwt");
            throw new InvalidTokenException("Expired token");
        } catch (UnsupportedJwtException ex) {
            log.error("Unsupported token");
            throw new InvalidTokenException("Unsupported token");
        } catch (IllegalArgumentException ex) {
            log.error("JWT claims empty");
            throw new InvalidTokenException("Empty jwt claims");
        }
        return true;
    }
}
