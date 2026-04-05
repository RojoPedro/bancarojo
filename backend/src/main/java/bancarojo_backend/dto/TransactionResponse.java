package bancarojo_backend.dto;

import bancarojo_backend.model.Transaction;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record TransactionResponse(
        UUID id,
        String referenceCode,
        UUID sourceAccountId,
        String sourceIban,
        UUID destAccountId,
        String destIban,
        BigDecimal amount,
        String currency,
        String type,
        String status,
        String description,
        LocalDateTime createdAt,
        LocalDateTime executedAt
) {
    public static TransactionResponse from(Transaction t) {
        return new TransactionResponse(
                t.getId(),
                t.getReferenceCode(),
                t.getSourceAccount().getId(),
                t.getSourceAccount().getIban(),
                t.getDestAccount().getId(),
                t.getDestAccount().getIban(),
                t.getAmount(),
                t.getCurrency(),
                t.getType().name(),
                t.getStatus().name(),
                t.getDescription(),
                t.getCreatedAt(),
                t.getExecutedAt()
        );
    }
}