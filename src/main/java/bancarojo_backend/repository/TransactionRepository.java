package bancarojo_backend.repository;

import bancarojo_backend.model.Account;
import bancarojo_backend.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {

    List<Transaction> findBySourceAccountOrDestAccount(
            Account sourceAccount,
            Account destAccount
    );

    List<Transaction> findBySourceAccountOrDestAccountOrderByCreatedAtDesc(
            Account sourceAccount,
            Account destAccount
    );

    List<Transaction> findByCreatedAtBetween(
            LocalDateTime from,
            LocalDateTime to
    );

    int countBySourceAccountAndCreatedAtBetween(
            Account sourceAccount,
            LocalDateTime from,
            LocalDateTime to
    );
}