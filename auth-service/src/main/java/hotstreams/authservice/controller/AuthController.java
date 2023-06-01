package hotstreams.authservice.controller;

import hotstreams.authservice.entity.User;
import hotstreams.authservice.model.ApiResponse;
import hotstreams.authservice.model.AuthenticationResponse;
import hotstreams.authservice.model.LoginRequest;
import hotstreams.authservice.model.RegistrationRequest;
import hotstreams.authservice.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping(value = "/register")
    public ResponseEntity<ApiResponse> registerNewUser(@Valid @RequestBody RegistrationRequest registrationRequest) {
        final User user = authService.registerUser(registrationRequest);
        return ResponseEntity.ok(new ApiResponse("success", "User registered " + user.getUsername()));
    }

    @PostMapping(value = "/authenticate", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AuthenticationResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        final AuthenticationResponse authenticationResponse = authService.authenticateUser(loginRequest);
        return ResponseEntity.ok(authenticationResponse);
    }
}
