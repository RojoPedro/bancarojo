package bancarojo_backend.controller;

import bancarojo_backend.dto.TransactionResponse;
import bancarojo_backend.dto.TransferRequest;
import bancarojo_backend.model.Transaction;
import bancarojo_backend.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
@Tag(name = "Transazioni", description = "Bonifici e cronologia movimenti")
@SecurityRequirement(name = "bearerAuth")
public class TransactionController {

    private final TransactionService transactionService;

    @Operation(
            summary = "Esegui bonifico",
            description = "Trasferisce un importo da un conto sorgente a un conto destinazione. " +
                    "Attiva automaticamente il sistema di fraud detection."
    )
    @PostMapping("/transfer")
    public ResponseEntity<TransactionResponse> transfer(
            @Valid @RequestBody TransferRequest request) {
        Transaction transaction = transactionService.transfer(
                request.sourceAccountId(),
                request.destAccountId(),
                request.amount(),
                request.description()
        );
        return ResponseEntity.ok(TransactionResponse.from(transaction));
    }

    @Operation(
            summary = "Cronologia movimenti",
            description = "Restituisce tutte le transazioni di un conto, ordinate dalla più recente"
    )
    @GetMapping("/account/{accountId}")
    public ResponseEntity<List<TransactionResponse>> getHistory(
            @Parameter(description = "ID del conto di cui recuperare la cronologia")
            @PathVariable UUID accountId) {
        List<TransactionResponse> history = transactionService.findByAccount(accountId)
                .stream()
                .map(TransactionResponse::from)
                .toList();
        return ResponseEntity.ok(history);
    }

    @Operation(
            summary = "Dettaglio transazione",
            description = "Restituisce i dettagli di una singola transazione tramite il suo ID"
    )
    @GetMapping("/{id}")
    public ResponseEntity<TransactionResponse> getTransaction(
            @Parameter(description = "ID della transazione") @PathVariable UUID id) {
        return transactionService.findById(id)
                .map(TransactionResponse::from)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}