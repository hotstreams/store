package hotstreams.authservice.service;

import hotstreams.authservice.entity.RefreshToken;
import hotstreams.authservice.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.sql.Ref;
import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    @Value("${auth.jwt.refresh.duration}")
    private Long refreshDuration;

    private final RefreshTokenRepository refreshTokenRepository;

    public void deleteRefreshTokenById(final Long tokenId) {
        refreshTokenRepository.deleteById(tokenId);
    }

    public RefreshToken save(final RefreshToken token) {
        return refreshTokenRepository.save(token);
    }

    public RefreshToken createRefreshToken() {
        return RefreshToken.builder()
                .expirationAt(Instant.now().plusMillis(refreshDuration))
                .token(UUID.randomUUID().toString())
                .build();
    }
}
