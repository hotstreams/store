package hotstreams.authservice.security;

import hotstreams.authservice.model.JwtUserDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class JwtTokenProvider {
    @Value("${auth.jwt.secret}")
    private String jwtSecret;

    @Value("${auth.jwt.expiration}")
    private long expiration;

    public String getUsernameFromToken(final String token) {
        final String subject = Jwts.parserBuilder()
                .setSigningKey(jwtSecret.getBytes())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();

        return Objects.requireNonNull(subject);
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

    public String createToken(final UserDetails userDetails) {
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(Date.from(Instant.now()))
                .setExpiration(Date.from(Instant.now().plusMillis(expiration)))
                .signWith(SignatureAlgorithm.HS256, jwtSecret.getBytes())
                .claim("authorities", getAuthorities(userDetails))
                .claim("userId", ((JwtUserDetails)userDetails).getUser().getId())
                .compact();
    }

    public String getAuthorities(final UserDetails userDetails) {
        return userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
    }
}
