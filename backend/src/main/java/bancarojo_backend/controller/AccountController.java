package bancarojo_backend.controller;

import bancarojo_backend.dto.AccountResponse;
import bancarojo_backend.dto.CreateAccountRequest;
import bancarojo_backend.model.Account;
import bancarojo_backend.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
@Tag(name = "Conti", description = "Apertura, gestione e chiusura conti bancari, inclusi prelievi e versamenti")
@SecurityRequirement(name = "bearerAuth")
public class AccountController {

    private final AccountService accountService;

    @Operation(
            summary = "Dettaglio conto",
            description = "Restituisce i dettagli di un conto tramite il suo ID"
    )
    @GetMapping("/{id}")
    public ResponseEntity<AccountResponse> getAccount(
            @Parameter(description = "ID del conto") @PathVariable UUID id) {
        return accountService.findById(id)
                .map(AccountResponse::from)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "Versamento",
            description = "Aggiunge un importo al saldo del conto specificato"
    )
    @PostMapping("/{id}/deposit")
    public ResponseEntity<AccountResponse> deposit(
            @Parameter(description = "ID del conto") @PathVariable UUID id,
            @Parameter(description = "Importo da versare (es. 500.00)") @RequestParam BigDecimal amount) {
        Account updatedAccount = accountService.deposit(id, amount);
        return ResponseEntity.ok(AccountResponse.from(updatedAccount));
    }

    @Operation(
            summary = "Prelievo",
            description = "Sottrae un importo dal saldo del conto specificato"
    )
    @PostMapping("/{id}/withdraw")
    public ResponseEntity<AccountResponse> withdraw(
            @Parameter(description = "ID del conto") @PathVariable UUID id,
            @Parameter(description = "Importo da prelevare (es. 200.00)") @RequestParam BigDecimal amount) {
        Account updatedAccount = accountService.withdraw(id, amount);
        return ResponseEntity.ok(AccountResponse.from(updatedAccount));
    }

    @Operation(
            summary = "Chiudi conto",
            description = "Chiusura logica del conto — il record rimane nel database con stato CLOSED"
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> closeAccount(
            @Parameter(description = "ID del conto da chiudere") @PathVariable UUID id) {
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