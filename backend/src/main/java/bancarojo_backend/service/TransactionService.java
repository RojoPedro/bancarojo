package bancarojo_backend.service;

import bancarojo_backend.exception.AccountNotActiveException;
import bancarojo_backend.exception.InsufficientFundsException;
import bancarojo_backend.exception.ResourceNotFoundException;
import bancarojo_backend.model.Account;
import bancarojo_backend.model.Transaction;
import bancarojo_backend.repository.AccountRepository;
import bancarojo_backend.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final FraudDetectionService fraudDetectionService;

    public Optional<Transaction> findById(UUID id) {
        return transactionRepository.findById(id);
    }

    public List<Transaction> findByAccount(UUID accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Conto non trovato"));

        return transactionRepository
                .findBySourceAccountOrDestAccountOrderByCreatedAtDesc(
                        account, account
                );
    }

    @Transactional
    public Transaction transfer(
            UUID sourceAccountId,
            UUID destAccountId,
            BigDecimal amount,
            String description
    ) {
        //Mi assicuro che esista l'account sorgente
        Account source = accountRepository.findById(sourceAccountId)
                .orElseThrow(() -> new ResourceNotFoundException("Conto sorgente non trovato"));

        //Mi assicuro che esista l'account destinazione
        Account dest = accountRepository.findById(destAccountId)
                .orElseThrow(() -> new ResourceNotFoundException("Conto destinazione non trovato"));

        validateTransfer(source, dest, amount);

        source.setBalance(source.getBalance().subtract(amount));
        dest.setBalance(dest.getBalance().add(amount));

        accountRepository.save(source);
        accountRepository.save(dest);

        Transaction transaction = Transaction.builder()
                .sourceAccount(source)
                .destAccount(dest)
                .amount(amount)
                .currency("EUR")
                .type(Transaction.TransactionType.TRANSFER)
                .status(Transaction.TransactionStatus.COMPLETED)
                .description(description)
                .referenceCode(generateReferenceCode())
                .executedAt(LocalDateTime.now())
                .build();

        Transaction saved = transactionRepository.save(transaction);

        fraudDetectionService.analyze(saved);

        return saved;
    }

    @Transactional
    public Transaction deposit(
            UUID destAccountId,
            UUID cashAccountId,
            BigDecimal amount,
            String description
    ) {
        Account dest = accountRepository.findById(destAccountId)
                .orElseThrow(() -> new ResourceNotFoundException("Conto non trovato"));

        Account cashAccount = accountRepository.findById(cashAccountId)
                .orElseThrow(() -> new ResourceNotFoundException("Conto cassa non trovato"));

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("L'importo deve essere positivo");
        }

        if (dest.getStatus() != Account.AccountStatus.ACTIVE) {
            throw new AccountNotActiveException("Il conto destinazione non è attivo");
        }

        cashAccount.setBalance(cashAccount.getBalance().subtract(amount));
        dest.setBalance(dest.getBalance().add(amount));

        accountRepository.save(cashAccount);
        accountRepository.save(dest);

        Transaction transaction = Transaction.builder()
                .sourceAccount(cashAccount)
                .destAccount(dest)
                .amount(amount)
                .currency("EUR")
                .type(Transaction.TransactionType.DEPOSIT)
                .status(Transaction.TransactionStatus.COMPLETED)
                .description(description)
                .referenceCode(generateReferenceCode())
                .executedAt(LocalDateTime.now())
                .build();

        Transaction saved = transactionRepository.save(transaction);

        fraudDetectionService.analyze(saved);

        return saved;
    }

    private void validateTransfer(
            Account source,
            Account dest,
            BigDecimal amount
    ) {
        //controllo che entrambi gli account siano in stato "ACTIVE"
        if (source.getStatus() != Account.AccountStatus.ACTIVE) {
            throw new AccountNotActiveException("Il conto sorgente non è attivo");
        }

        if (dest.getStatus() != Account.AccountStatus.ACTIVE) {
            throw new AccountNotActiveException("Il conto destinazione non è attivo");
        }

        //Verifico che l'importo della transazione sia positivo
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("L'importo deve essere positivo");
        }
        //Controllo che l'account sorgente abbia un blance
        if (source.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException("Saldo insufficiente");
        }

        if (amount.compareTo(new BigDecimal("10000")) > 0) {
            throw new InsufficientFundsException("Importo supera il limite giornaliero di 10.000€");
        }
    }

    private String generateReferenceCode() {
        return "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}