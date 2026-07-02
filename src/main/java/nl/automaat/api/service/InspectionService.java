package nl.automaat.api.service;

import jakarta.persistence.EntityNotFoundException;
import nl.automaat.api.dto.InspectionRequestDto;
import nl.automaat.api.dto.InspectionResponseDto;
import nl.automaat.api.model.Car;
import nl.automaat.api.model.Inspection;
import nl.automaat.api.repository.CarRepository;
import nl.automaat.api.repository.InspectionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InspectionService {

    private final InspectionRepository inspectionRepository;
    private final CarRepository carRepository;

    public InspectionService(InspectionRepository inspectionRepository, CarRepository carRepository) {
        this.inspectionRepository = inspectionRepository;
        this.carRepository = carRepository;
    }

    public List<InspectionResponseDto> getAll() {
        return inspectionRepository.findAll().stream().map(this::toDto).toList();
    }

    public InspectionResponseDto getById(Long id) {
        return toDto(findOrThrow(id));
    }

    public InspectionResponseDto create(InspectionRequestDto dto) {
        Inspection inspection = new Inspection();
        apply(inspection, dto);
        return toDto(inspectionRepository.save(inspection));
    }

    public InspectionResponseDto update(Long id, InspectionRequestDto dto) {
        Inspection inspection = findOrThrow(id);
        apply(inspection, dto);
        return toDto(inspectionRepository.save(inspection));
    }

    public void delete(Long id) {
        inspectionRepository.delete(findOrThrow(id));
    }

    public Inspection findOrThrow(Long id) {
        return inspectionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Keuring met id " + id + " niet gevonden."));
    }

    private void apply(Inspection inspection, InspectionRequestDto dto) {
        // keuring hangt verplicht aan een bestaande auto
        Car car = carRepository.findById(dto.getCarId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Auto met id " + dto.getCarId() + " niet gevonden."));
        inspection.setCar(car);
        inspection.setInspectionDate(dto.getInspectionDate());
        inspection.setStatus(dto.getStatus());
        inspection.setIssues(dto.getIssues());
    }

    private InspectionResponseDto toDto(Inspection inspection) {
        InspectionResponseDto dto = new InspectionResponseDto();
        dto.setId(inspection.getId());
        dto.setCarId(inspection.getCar() != null ? inspection.getCar().getId() : null);
        dto.setInspectionDate(inspection.getInspectionDate());
        dto.setStatus(inspection.getStatus());
        dto.setIssues(inspection.getIssues());
        return dto;
    }
}
