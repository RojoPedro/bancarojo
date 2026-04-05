package bancarojo_backend.dto;

import bancarojo_backend.model.Account;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record CreateAccountRequest(

        @NotNull(message = "L'ID utente è obbligatorio")
        UUID userId,

        @NotNull(message = "Il tipo di conto è obbligatorio")
        Account.AccountType accountType
) {}