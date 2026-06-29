package nl.automaat.api.service;

import jakarta.persistence.EntityNotFoundException;
import nl.automaat.api.dto.PartRequestDto;
import nl.automaat.api.dto.PartResponseDto;
import nl.automaat.api.model.Part;
import nl.automaat.api.repository.PartRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PartService {

    private final PartRepository partRepository;

    public PartService(PartRepository partRepository) {
        this.partRepository = partRepository;
    }

    public List<PartResponseDto> getAll() {
        return partRepository.findAll().stream().map(this::toDto).toList();
    }

    public PartResponseDto getById(Long id) {
        return toDto(findOrThrow(id));
    }

    public PartResponseDto create(PartRequestDto dto) {
        Part part = new Part();
        apply(part, dto);
        return toDto(partRepository.save(part));
    }

    public PartResponseDto update(Long id, PartRequestDto dto) {
        Part part = findOrThrow(id);
        apply(part, dto);
        return toDto(partRepository.save(part));
    }

    public void delete(Long id) {
        partRepository.delete(findOrThrow(id));
    }

    public Part findOrThrow(Long id) {
        return partRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Onderdeel met id " + id + " niet gevonden."));
    }

    private void apply(Part part, PartRequestDto dto) {
        part.setName(dto.getName());
        part.setPrice(dto.getPrice());
        part.setStockQuantity(dto.getStockQuantity());
    }

    private PartResponseDto toDto(Part part) {
        PartResponseDto dto = new PartResponseDto();
        dto.setId(part.getId());
        dto.setName(part.getName());
        dto.setPrice(part.getPrice());
        dto.setStockQuantity(part.getStockQuantity());
        return dto;
    }
}
