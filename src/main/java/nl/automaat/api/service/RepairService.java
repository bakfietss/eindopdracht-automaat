package nl.automaat.api.service;

import jakarta.persistence.EntityNotFoundException;
import nl.automaat.api.dto.RepairRequestDto;
import nl.automaat.api.dto.RepairResponseDto;
import nl.automaat.api.model.Car;
import nl.automaat.api.model.Part;
import nl.automaat.api.model.Repair;
import nl.automaat.api.repository.CarRepository;
import nl.automaat.api.repository.PartRepository;
import nl.automaat.api.repository.RepairRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RepairService {

    private final RepairRepository repairRepository;
    private final CarRepository carRepository;
    private final PartRepository partRepository;

    public RepairService(RepairRepository repairRepository,
                         CarRepository carRepository,
                         PartRepository partRepository) {
        this.repairRepository = repairRepository;
        this.carRepository = carRepository;
        this.partRepository = partRepository;
    }

    public List<RepairResponseDto> getAll() {
        return repairRepository.findAll().stream().map(this::toDto).toList();
    }

    public RepairResponseDto getById(Long id) {
        return toDto(findOrThrow(id));
    }

    public RepairResponseDto create(RepairRequestDto dto) {
        Repair repair = new Repair();
        apply(repair, dto);
        return toDto(repairRepository.save(repair));
    }

    public RepairResponseDto update(Long id, RepairRequestDto dto) {
        Repair repair = findOrThrow(id);
        apply(repair, dto);
        return toDto(repairRepository.save(repair));
    }

    public void delete(Long id) {
        repairRepository.delete(findOrThrow(id));
    }

    public Repair findOrThrow(Long id) {
        return repairRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Reparatie met id " + id + " niet gevonden."));
    }

    private void apply(Repair repair, RepairRequestDto dto) {
        Car car = carRepository.findById(dto.getCarId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Auto met id " + dto.getCarId() + " niet gevonden."));
        repair.setCar(car);
        repair.setRepairDate(dto.getRepairDate());
        repair.setStatus(dto.getStatus());
        repair.setNotes(dto.getNotes());

        if (dto.getPartIds() != null) {
            // elk id moet echt bestaan, anders 404
            List<Part> parts = dto.getPartIds().stream()
                    .map(partId -> partRepository.findById(partId)
                            .orElseThrow(() -> new EntityNotFoundException(
                                    "Onderdeel met id " + partId + " niet gevonden.")))
                    .toList();
            repair.setParts(parts);
        }
    }

    private RepairResponseDto toDto(Repair repair) {
        RepairResponseDto dto = new RepairResponseDto();
        dto.setId(repair.getId());
        dto.setCarId(repair.getCar() != null ? repair.getCar().getId() : null);
        dto.setRepairDate(repair.getRepairDate());
        dto.setStatus(repair.getStatus());
        dto.setNotes(repair.getNotes());
        dto.setParts(repair.getParts().stream().map(PartService::toDto).toList());
        return dto;
    }
}
