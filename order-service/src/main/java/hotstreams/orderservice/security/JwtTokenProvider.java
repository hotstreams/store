package hotstreams.orderservice.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class JwtTokenProvider {
    @Value("${auth.jwt.secret}")
    private String jwtSecret;

    public String getUserIdFromToken(final String token) {
        final String userId = Jwts.parserBuilder()
                .setSigningKey(jwtSecret.getBytes())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("userId", String.class);
        return Objects.requireNonNull(userId);
    }

    public List<GrantedAuthority> getAuthoritiesFromToken(final String token) {
        final Claims claims = Jwts.parserBuilder()
                .setSigningKey(jwtSecret.getBytes())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return Arrays.stream(claims.get("authorities").toString().split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }
}
