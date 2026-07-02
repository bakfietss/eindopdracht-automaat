package nl.automaat.api.controller;

import nl.automaat.api.dto.InspectionRequestDto;
import nl.automaat.api.dto.InspectionResponseDto;
import nl.automaat.api.service.InspectionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/inspections")
public class InspectionController {

    private final InspectionService inspectionService;

    public InspectionController(InspectionService inspectionService) {
        this.inspectionService = inspectionService;
    }

    @GetMapping
    public ResponseEntity<List<InspectionResponseDto>> getAll() {
        return ResponseEntity.ok(inspectionService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<InspectionResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(inspectionService.getById(id));
    }

    @PostMapping
    public ResponseEntity<InspectionResponseDto> create(@RequestBody InspectionRequestDto dto) {
        InspectionResponseDto created = inspectionService.create(dto);
        return ResponseEntity.created(URI.create("/inspections/" + created.getId())).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<InspectionResponseDto> update(@PathVariable Long id, @RequestBody InspectionRequestDto dto) {
        return ResponseEntity.ok(inspectionService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        inspectionService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
