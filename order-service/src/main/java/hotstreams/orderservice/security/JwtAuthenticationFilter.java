package hotstreams.orderservice.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Value("${auth.jwt.header}")
    private String tokenHeader;

    @Value("${auth.jwt.header.prefix}")
    private String tokenHeaderPrefix;

    private final JwtTokenValidator jwtTokenValidator;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            final String jwt = getTokenFromHeader(request);

            if (StringUtils.hasText(jwt) && jwtTokenValidator.validate(jwt)) {
                final String userId = jwtTokenProvider.getUserIdFromToken(jwt);
                final List<GrantedAuthority> authorities = jwtTokenProvider.getAuthoritiesFromToken(jwt);

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userId, jwt, authorities);
                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.info("Request authenticated");
            }
        } catch (Exception ex) {
            log.error("Authentication failed", ex);
        }

        filterChain.doFilter(request, response);
    }

    private String getTokenFromHeader(final HttpServletRequest request) {
        final String header = request.getHeader(tokenHeader);
        if (StringUtils.hasText(header) && header.startsWith(tokenHeaderPrefix)) {
            return header.substring(tokenHeaderPrefix.length());
        }
        return null;
    }
}
