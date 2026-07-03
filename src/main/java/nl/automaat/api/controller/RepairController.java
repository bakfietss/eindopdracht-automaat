package nl.automaat.api.controller;

import nl.automaat.api.dto.RepairRequestDto;
import nl.automaat.api.dto.RepairResponseDto;
import nl.automaat.api.service.RepairService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/repairs")
public class RepairController {

    private final RepairService repairService;

    public RepairController(RepairService repairService) {
        this.repairService = repairService;
    }

    @GetMapping
    public ResponseEntity<List<RepairResponseDto>> getAll() {
        return ResponseEntity.ok(repairService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RepairResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(repairService.getById(id));
    }

    @PostMapping
    public ResponseEntity<RepairResponseDto> create(@RequestBody RepairRequestDto dto) {
        RepairResponseDto created = repairService.create(dto);
        return ResponseEntity.created(URI.create("/repairs/" + created.getId())).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RepairResponseDto> update(@PathVariable Long id, @RequestBody RepairRequestDto dto) {
        return ResponseEntity.ok(repairService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        repairService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
