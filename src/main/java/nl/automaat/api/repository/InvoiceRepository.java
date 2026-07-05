package nl.automaat.api.repository;

import nl.automaat.api.model.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    boolean existsByRepairId(Long repairId);

    Optional<Invoice> findByRepairId(Long repairId);
}
