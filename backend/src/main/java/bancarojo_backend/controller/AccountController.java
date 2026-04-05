package bancarojo_backend.controller;

// DTO
import bancarojo_backend.dto.AccountResponse;
import bancarojo_backend.dto.CreateAccountRequest;

// Model
import bancarojo_backend.model.Account;

// Service
import bancarojo_backend.service.AccountService;

// Swagger / OpenAPI
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

// Jakarta (validazione)
import jakarta.validation.Valid;

// Lombok
import lombok.RequiredArgsConstructor;

// Spring HTTP
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

// Spring Web
import org.springframework.web.bind.annotation.*;

// Java
import java.math.BigDecimal;
import java.util.UUID;


@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    // Recupera i dettagli di un conto specifico
    @GetMapping("/{id}")
    public ResponseEntity<AccountResponse> getAccount(@PathVariable UUID id) {
        return accountService.findById(id)
                .map(AccountResponse::from)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Versamento sul conto
    @PostMapping("/{id}/deposit")
    public ResponseEntity<AccountResponse> deposit(
            @PathVariable UUID id,
            @RequestParam BigDecimal amount) {
        Account updatedAccount = accountService.deposit(id, amount);
        return ResponseEntity.ok(AccountResponse.from(updatedAccount));
    }

    // Prelievo dal conto
    @PostMapping("/{id}/withdraw")
    public ResponseEntity<AccountResponse> withdraw(
            @PathVariable UUID id,
            @RequestParam BigDecimal amount) {
        Account updatedAccount = accountService.withdraw(id, amount);
        return ResponseEntity.ok(AccountResponse.from(updatedAccount));
    }

    // Chiusura del conto (operazione logica, non fisica)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> closeAccount(@PathVariable UUID id) {
        accountService.closeAccount(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Apri nuovo conto",
            description = "Crea un nuovo conto bancario per un utente esistente. " +
                    "L'IBAN viene generato automaticamente dal sistema."
    )
    @PostMapping
    public ResponseEntity<AccountResponse> createAccount(
            @Valid @RequestBody CreateAccountRequest request) {
        Account account = accountService.create(
                request.userId(),
                request.accountType()
        );
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(AccountResponse.from(account));
    }
}