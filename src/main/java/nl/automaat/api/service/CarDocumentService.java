package nl.automaat.api.service;

import jakarta.persistence.EntityNotFoundException;
import nl.automaat.api.model.Car;
import nl.automaat.api.repository.CarRepository;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class CarDocumentService {

    private final CarRepository carRepository;
    private final FileStorageService fileStorageService;

    public CarDocumentService(CarRepository carRepository, FileStorageService fileStorageService) {
        this.carRepository = carRepository;
        this.fileStorageService = fileStorageService;
    }

    public String uploadDocument(Long carId, MultipartFile file) {
        Car car = findOrThrow(carId);
        String storedPath = fileStorageService.store(file);
        car.setRegistrationDocumentPath(storedPath);
        carRepository.save(car);
        return storedPath;
    }

    public Resource downloadDocument(Long carId) {
        Car car = findOrThrow(carId);
        if (car.getRegistrationDocumentPath() == null) {
            throw new EntityNotFoundException("Auto " + carId + " heeft geen autopapieren.");
        }
        return fileStorageService.loadAsResource(car.getRegistrationDocumentPath());
    }

    private Car findOrThrow(Long carId) {
        return carRepository.findById(carId)
                .orElseThrow(() -> new EntityNotFoundException("Auto met id " + carId + " niet gevonden."));
    }
}
