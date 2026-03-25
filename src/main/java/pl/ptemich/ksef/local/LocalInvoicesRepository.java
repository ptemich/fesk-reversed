package pl.ptemich.ksef.local;

import org.springframework.data.jpa.repository.JpaRepository;

public interface LocalInvoicesRepository extends JpaRepository<LocalInvoice, String> {
}
