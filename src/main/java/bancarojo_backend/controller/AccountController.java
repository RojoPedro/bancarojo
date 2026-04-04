package bancarojo_backend.controller;

import bancarojo_backend.dto.AccountResponse;
import bancarojo_backend.model.Account;
import bancarojo_backend.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}