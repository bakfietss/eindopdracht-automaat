package nl.automaat.api.repository;

import nl.automaat.api.model.Car;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CarRepository extends JpaRepository<Car, Long> {
    boolean existsByLicensePlate(String licensePlate);

    List<Car> findByCustomerId(Long customerId);
}
