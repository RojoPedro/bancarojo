package bancarojo_backend.repository;

import bancarojo_backend.model.FraudAlert;
import bancarojo_backend.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FraudAlertRepository extends JpaRepository<FraudAlert, UUID> {

    Optional<FraudAlert> findByTransaction(Transaction transaction);

    List<FraudAlert> findBySeverityOrderByDetectedAtDesc(
            FraudAlert.Severity severity
    );

    List<FraudAlert> findByStatusOrderByDetectedAtDesc(
            FraudAlert.AlertStatus status
    );
}