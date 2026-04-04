package bancarojo_backend.service;

import bancarojo_backend.exception.InsufficientFundsException;
import bancarojo_backend.exception.ResourceNotFoundException;
import bancarojo_backend.model.Account;
import bancarojo_backend.model.User;
import bancarojo_backend.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;

    public Optional<Account> findById(UUID id) {
        return accountRepository.findById(id);
    }

    public Optional<Account> findByIban(String iban) {
        return accountRepository.findByIban(iban);
    }

    public List<Account> findByUser(User user) {
        return accountRepository.findByUser(user);
    }

    @Transactional
    public Account save(Account account) {
        return accountRepository.save(account);
    }

    @Transactional
    public Account deposit(UUID accountId, BigDecimal amount) {

        // Prima: RuntimeException generica → errore 500
        // Ora: ResourceNotFoundException → errore 404 pulito
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Conto non trovato con id: " + accountId));

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            // IllegalArgumentException → gestita dal GlobalExceptionHandler come 400
            throw new IllegalArgumentException("L'importo deve essere positivo");
        }

        account.setBalance(account.getBalance().add(amount));
        return accountRepository.save(account);
    }

    @Transactional
    public Account withdraw(UUID accountId, BigDecimal amount) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Conto non trovato"));

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("L'importo deve essere positivo");
        }

        if (account.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException("Saldo insufficiente");
        }

        account.setBalance(account.getBalance().subtract(amount));
        return accountRepository.save(account);
    }

    @Transactional
    public void closeAccount(UUID accountId) {
        accountRepository.findById(accountId).ifPresent(account -> {
            account.setStatus(Account.AccountStatus.CLOSED);
            accountRepository.save(account);
        });
    }
}