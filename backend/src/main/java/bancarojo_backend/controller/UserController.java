package bancarojo_backend.controller;

import bancarojo_backend.dto.UserResponse;
import bancarojo_backend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "Utenti", description = "Gestione profilo utente")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserService userService;

    @Operation(
            summary = "Profilo utente",
            description = "Restituisce i dati del profilo — la passwordHash è esclusa dalla risposta"
    )
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUser(
            @Parameter(description = "ID dell'utente") @PathVariable UUID id) {
        return userService.findById(id)
                .map(UserResponse::from)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "Disattiva utente",
            description = "Disattivazione logica — il record rimane nel database con isActive=false"
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deactivateUser(
            @Parameter(description = "ID dell'utente da disattivare") @PathVariable UUID id) {
        userService.deactivate(id);
        return ResponseEntity.noContent().build();
    }
}