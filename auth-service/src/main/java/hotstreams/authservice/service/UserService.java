package hotstreams.authservice.service;

import hotstreams.authservice.entity.Role;
import hotstreams.authservice.entity.RoleName;
import hotstreams.authservice.entity.User;
import hotstreams.authservice.model.RegistrationRequest;
import hotstreams.authservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User createUser(final RegistrationRequest registrationRequest) {
        User user = User.builder()
                .id(UUID.randomUUID().toString())
                .username(registrationRequest.getUsername())
                .password(passwordEncoder.encode(registrationRequest.getPassword()))
                .email(registrationRequest.getEmail())
                .roles(getRolesForNewUser())
                .build();
        return userRepository.save(user);
    }

    public Boolean existsByEmail(final String email) {
        return userRepository.existsByEmail(email);
    }

    private Set<Role> getRolesForNewUser() {
        return Set.of(Role.builder().roleName(RoleName.USER).build());
    }
}
