package nl.automaat.api.repository;

import nl.automaat.api.model.Repair;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RepairRepository extends JpaRepository<Repair, Long> {
    List<Repair> findByCarId(Long carId);
}
