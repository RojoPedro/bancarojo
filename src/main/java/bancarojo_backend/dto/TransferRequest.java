package bancarojo_backend.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;

public record TransferRequest(

        @NotNull(message = "Il conto sorgente è obbligatorio")
        UUID sourceAccountId,

        @NotNull(message = "Il conto destinazione è obbligatorio")
        UUID destAccountId,

        @NotNull(message = "L'importo è obbligatorio")
        @DecimalMin(value = "0.01", message = "L'importo minimo è 0.01€")
        BigDecimal amount,

        String description
) {}