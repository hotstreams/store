package hotstreams.authservice.service;

import hotstreams.authservice.entity.Device;
import hotstreams.authservice.entity.RefreshToken;
import hotstreams.authservice.entity.User;
import hotstreams.authservice.exception.EmailAlreadyExistsException;
import hotstreams.authservice.model.*;
import hotstreams.authservice.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final DeviceService deviceService;
    private final RefreshTokenService refreshTokenService;
    private final JwtTokenProvider tokenProvider;

    public User registerUser(final RegistrationRequest registrationRequest) {
        if (userService.existsByEmail(registrationRequest.getEmail())) {
            log.info("Email already exists " + registrationRequest.getEmail());
            throw new EmailAlreadyExistsException(registrationRequest.getEmail());
        }
        final User user = userService.createUser(registrationRequest);
        log.info("User registered " + user.getUsername());
        return user;
    }

    public AuthenticationResponse authenticateUser(final LoginRequest loginRequest) {
        final Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        final RefreshToken refreshToken = createRefreshTokenForDevice(authentication, loginRequest);
        final String jwt = tokenProvider.createToken((UserDetails) authentication.getPrincipal());
        return new AuthenticationResponse(jwt, refreshToken.getToken());
    }

    private RefreshToken createRefreshTokenForDevice(final Authentication authentication, final LoginRequest loginRequest) {
        final JwtUserDetails userDetails = (JwtUserDetails) authentication.getPrincipal();
        removeRefreshToken(userDetails, loginRequest.getDeviceInfo());

        final Device device = deviceService.createDevice(loginRequest.getDeviceInfo());
        final RefreshToken refreshToken = refreshTokenService.createRefreshToken();
        refreshToken.setDevice(device);
        device.setUser(userDetails.getUser());
        device.setRefreshToken(refreshToken);
        return refreshTokenService.save(refreshToken);
    }

    private void removeRefreshToken(final JwtUserDetails user, final DeviceInfo deviceInfo) {
        deviceService.findDeviceByUserId(user.getUser().getId(), deviceInfo.getDeviceId())
                .map(Device::getRefreshToken)
                .map(RefreshToken::getId)
                .ifPresent(refreshTokenService::deleteRefreshTokenById);
    }
}
