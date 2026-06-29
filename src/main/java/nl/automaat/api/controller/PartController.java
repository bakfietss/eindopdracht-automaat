package nl.automaat.api.controller;

import nl.automaat.api.dto.PartRequestDto;
import nl.automaat.api.dto.PartResponseDto;
import nl.automaat.api.service.PartService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/parts")
public class PartController {

    private final PartService partService;

    public PartController(PartService partService) {
        this.partService = partService;
    }

    @GetMapping
    public ResponseEntity<List<PartResponseDto>> getAll() {
        return ResponseEntity.ok(partService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PartResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(partService.getById(id));
    }

    @PostMapping
    public ResponseEntity<PartResponseDto> create(@RequestBody PartRequestDto dto) {
        PartResponseDto created = partService.create(dto);
        return ResponseEntity.created(URI.create("/parts/" + created.getId())).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PartResponseDto> update(@PathVariable Long id, @RequestBody PartRequestDto dto) {
        return ResponseEntity.ok(partService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        partService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
