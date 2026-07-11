package nl.automaat.api.service;

import jakarta.persistence.EntityNotFoundException;
import nl.automaat.api.dto.CarRequestDto;
import nl.automaat.api.dto.CarResponseDto;
import nl.automaat.api.dto.CarUpdateDto;
import nl.automaat.api.model.Car;
import nl.automaat.api.model.Customer;
import nl.automaat.api.repository.CarRepository;
import nl.automaat.api.repository.CustomerRepository;
import nl.automaat.api.util.PatchUtil;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CarService {

    private final CarRepository carRepository;
    private final CustomerRepository customerRepository;

    public CarService(CarRepository carRepository, CustomerRepository customerRepository) {
        this.carRepository = carRepository;
        this.customerRepository = customerRepository;
    }

    public List<CarResponseDto> getAll() {
        return carRepository.findAll().stream().map(this::toDto).toList();
    }

    public CarResponseDto getById(Long id) {
        return toDto(findOrThrow(id));
    }

    public CarResponseDto create(CarRequestDto dto) {
        if (carRepository.existsByLicensePlate(dto.getLicensePlate())) {
            throw new IllegalArgumentException("Er bestaat al een auto met dit kenteken.");
        }
        Car car = new Car();
        apply(car, dto);
        return toDto(carRepository.save(car));
    }

    public CarResponseDto update(Long id, CarRequestDto dto) {
        Car car = findOrThrow(id);
        if (!car.getLicensePlate().equalsIgnoreCase(dto.getLicensePlate())
                && carRepository.existsByLicensePlate(dto.getLicensePlate())) {
            throw new IllegalArgumentException("Er bestaat al een auto met dit kenteken.");
        }
        apply(car, dto);
        return toDto(carRepository.save(car));
    }

    public CarResponseDto patch(Long id, CarUpdateDto dto) {
        Car car = findOrThrow(id);
        if (dto.getLicensePlate() != null
                && !car.getLicensePlate().equalsIgnoreCase(dto.getLicensePlate())
                && carRepository.existsByLicensePlate(dto.getLicensePlate())) {
            throw new IllegalArgumentException("Er bestaat al een auto met dit kenteken.");
        }
        PatchUtil.applyIfPresent(dto.getLicensePlate(), car::setLicensePlate);
        PatchUtil.applyIfPresent(dto.getBrand(), car::setBrand);
        PatchUtil.applyIfPresent(dto.getModel(), car::setModel);
        PatchUtil.applyIfPresent(dto.getBuildYear(), car::setBuildYear);
        if (dto.getCustomerId() != null) {
            Customer customer = customerRepository.findById(dto.getCustomerId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Klant met id " + dto.getCustomerId() + " niet gevonden."));
            car.setCustomer(customer);
        }
        return toDto(carRepository.save(car));
    }

    public void delete(Long id) {
        carRepository.delete(findOrThrow(id));
    }

    public Car findOrThrow(Long id) {
        return carRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Auto met id " + id + " niet gevonden."));
    }

    private void apply(Car car, CarRequestDto dto) {
        // auto hangt verplicht aan een bestaande klant
        Customer customer = customerRepository.findById(dto.getCustomerId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Klant met id " + dto.getCustomerId() + " niet gevonden."));
        car.setLicensePlate(dto.getLicensePlate());
        car.setBrand(dto.getBrand());
        car.setModel(dto.getModel());
        car.setBuildYear(dto.getBuildYear());
        car.setCustomer(customer);
    }

    private CarResponseDto toDto(Car car) {
        CarResponseDto dto = new CarResponseDto();
        dto.setId(car.getId());
        dto.setLicensePlate(car.getLicensePlate());
        dto.setBrand(car.getBrand());
        dto.setModel(car.getModel());
        dto.setBuildYear(car.getBuildYear());
        dto.setCustomerId(car.getCustomer() != null ? car.getCustomer().getId() : null);
        return dto;
    }
}
