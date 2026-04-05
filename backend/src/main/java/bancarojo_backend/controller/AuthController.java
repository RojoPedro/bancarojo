package bancarojo_backend.controller;

import bancarojo_backend.dto.AuthResponse;
import bancarojo_backend.dto.LoginRequest;
import bancarojo_backend.dto.RegisterRequest;
import bancarojo_backend.security.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;


@RestController
@RequestMapping("/api/auth") // Rotte pubbliche definite nel tuo SecurityConfig
@RequiredArgsConstructor
@Tag(name = "Autenticazione", description = "Registrazione, login e logout")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "Registra un nuovo utente",
        description = "Crea un account e restituisce un token JWT")
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        // Chiamiamo il service e restituiamo 200 OK con il token
        return ResponseEntity.ok(authService.register(request));
    }

    @Operation(summary = "Login",
            description = "Autentica l'utente e restituisce un token JWT")
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request.email(), request.password()));
    }

    @Operation(summary = "Logout",
            description = "Invalida il token JWT corrente tramite blacklist Redis")
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader("Authorization") String authHeader) {
        // Estraiamo il token dall'header per metterlo in blacklist su Redis
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            authService.logout(authHeader.substring(7));
        }
        return ResponseEntity.noContent().build(); // 204 No Content
    }
}

