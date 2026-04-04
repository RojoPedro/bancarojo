package bancarojo_backend.service;

import bancarojo_backend.model.FraudAlert;
import bancarojo_backend.model.Transaction;
import bancarojo_backend.repository.FraudAlertRepository;
import bancarojo_backend.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FraudDetectionService {

    private final FraudAlertRepository fraudAlertRepository;
    private final TransactionRepository transactionRepository;

    private static final BigDecimal LARGE_AMOUNT_THRESHOLD = new BigDecimal("5000");
    private static final int MAX_TRANSACTIONS_PER_HOUR = 5;

    @Transactional
    public void analyze(Transaction transaction) {
        checkLargeAmount(transaction);
        checkRapidSuccession(transaction);
    }

    private void checkLargeAmount(Transaction transaction) {
        if (transaction.getAmount().compareTo(LARGE_AMOUNT_THRESHOLD) > 0) {
            log.warn("Transazione di importo elevato rilevata: {}",
                    transaction.getReferenceCode());

            createAlert(
                    transaction,
                    FraudAlert.AlertType.UNUSUAL_AMOUNT,
                    FraudAlert.Severity.HIGH,
                    new BigDecimal("0.75"),
                    "Importo superiore a " + LARGE_AMOUNT_THRESHOLD + "€"
            );
        }
    }

    private void checkRapidSuccession(Transaction transaction) {
        LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);

        int recentCount = transactionRepository.countBySourceAccountAndCreatedAtBetween(
                transaction.getSourceAccount(),
                oneHourAgo,
                LocalDateTime.now()
        );

        if (recentCount > MAX_TRANSACTIONS_PER_HOUR) {
            log.warn("Transazioni in rapida successione rilevate per conto: {}",
                    transaction.getSourceAccount().getIban());

            createAlert(
                    transaction,
                    FraudAlert.AlertType.RAPID_SUCCESSION,
                    FraudAlert.Severity.MEDIUM,
                    new BigDecimal("0.60"),
                    recentCount + " transazioni nell'ultima ora"
            );
        }
    }

    private void createAlert(
            Transaction transaction,
            FraudAlert.AlertType alertType,
            FraudAlert.Severity severity,
            BigDecimal riskScore,
            String details
    ) {
        FraudAlert alert = FraudAlert.builder()
                .transaction(transaction)
                .alertType(alertType)
                .severity(severity)
                .riskScore(riskScore)
                .details(details)
                .status(FraudAlert.AlertStatus.OPEN)
                .build();

        fraudAlertRepository.save(alert);
    }

    public List<FraudAlert> findOpenAlerts() {
        return fraudAlertRepository
                .findByStatusOrderByDetectedAtDesc(FraudAlert.AlertStatus.OPEN);
    }

    public List<FraudAlert> findBySeverity(FraudAlert.Severity severity) {
        return fraudAlertRepository.findBySeverityOrderByDetectedAtDesc(severity);
    }
}