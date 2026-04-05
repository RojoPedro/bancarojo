package bancarojo_backend.dto;

import bancarojo_backend.model.Account;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record AccountResponse(
        UUID id,
        String iban,
        String accountType,
        BigDecimal balance,
        String currency,
        String status,
        LocalDateTime openedAt
) {
    public static AccountResponse from(Account account) {
        return new AccountResponse(
                account.getId(),
                account.getIban(),
                account.getAccountType().name(),
                account.getBalance(),
                account.getCurrency(),
                account.getStatus().name(),
                account.getOpenedAt()
        );
    }
}