package bancarojo_backend.security;

import bancarojo_backend.dto.AuthResponse;
import bancarojo_backend.dto.RegisterRequest;
import bancarojo_backend.exception.DuplicateResourceException;
import bancarojo_backend.model.User;
import bancarojo_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsServiceImpl userDetailsService;

    @Transactional
    public AuthResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.email())) {
            throw new DuplicateResourceException(
                    "Email già registrata: " + request.email());
        }

        User user = User.builder()
                .email(request.email())
                .passwordHash(passwordEncoder.encode(request.password()))
                .firstName(request.firstName())
                .lastName(request.lastName())
                .phone(request.phone())
                .role(User.Role.CUSTOMER)
                .isActive(true)
                .build();

        userRepository.save(user);

        UserDetails userDetails = userDetailsService
                .loadUserByUsername(user.getEmail());
        String token = jwtService.generateToken(userDetails);

        log.info("Nuovo utente registrato: {}", user.getEmail());

        return AuthResponse.of(
                token,
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getRole().name()
        );
    }

    public AuthResponse login(String email, String password) {

        // Delega a Spring Security la verifica delle credenziali
        // Lancia AuthenticationException automaticamente se errate
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );

        UserDetails userDetails =
                userDetailsService.loadUserByUsername(email);
        String token = jwtService.generateToken(userDetails);

        User user = userRepository.findByEmail(email).orElseThrow();

        log.info("Login effettuato: {}", email);

        return AuthResponse.of(
                token,
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getRole().name()
        );
    }

    public void logout(String token) {
        jwtService.invalidateToken(token);
        log.info("Logout effettuato, token invalidato");
    }
}