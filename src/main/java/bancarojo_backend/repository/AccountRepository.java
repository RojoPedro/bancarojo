package bancarojo_backend.repository;

import bancarojo_backend.model.Account;
import bancarojo_backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AccountRepository extends JpaRepository<Account, UUID> {

    List<Account> findByUser(User user);

    Optional<Account> findByIban(String iban);

    boolean existsByIban(String iban);
}