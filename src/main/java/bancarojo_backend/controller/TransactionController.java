package bancarojo_backend.controller;

import bancarojo_backend.dto.TransactionResponse;
import bancarojo_backend.dto.TransferRequest;
import bancarojo_backend.model.Transaction;
import bancarojo_backend.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    // Effettua un bonifico tra due conti
    @PostMapping("/transfer")
    public ResponseEntity<TransactionResponse> transfer(@Valid @RequestBody TransferRequest request) {
        Transaction transaction = transactionService.transfer(
                request.sourceAccountId(),
                request.destAccountId(),
                request.amount(),
                request.description()
        );
        return ResponseEntity.ok(TransactionResponse.from(transaction));
    }

    // Recupera la cronologia delle transazioni di un conto specifico
    @GetMapping("/account/{accountId}")
    public ResponseEntity<List<TransactionResponse>> getHistory(@PathVariable UUID accountId) {
        List<TransactionResponse> history = transactionService.findByAccount(accountId)
                .stream()
                .map(TransactionResponse::from)
                .toList();
        return ResponseEntity.ok(history);
    }

    // Dettaglio di una singola transazione tramite ID
    @GetMapping("/{id}")
    public ResponseEntity<TransactionResponse> getTransaction(@PathVariable UUID id) {
        return transactionService.findById(id)
                .map(TransactionResponse::from)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}