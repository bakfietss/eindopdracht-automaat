package nl.automaat.api.controller;

import jakarta.validation.Valid;
import nl.automaat.api.dto.CarRequestDto;
import nl.automaat.api.dto.CarResponseDto;
import nl.automaat.api.dto.CarUpdateDto;
import nl.automaat.api.service.CarDocumentService;
import nl.automaat.api.service.CarService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/cars")
public class CarController {

    private final CarService carService;
    private final CarDocumentService carDocumentService;

    public CarController(CarService carService, CarDocumentService carDocumentService) {
        this.carService = carService;
        this.carDocumentService = carDocumentService;
    }

    @GetMapping
    public ResponseEntity<List<CarResponseDto>> getAll() {
        return ResponseEntity.ok(carService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CarResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(carService.getById(id));
    }

    @PostMapping
    public ResponseEntity<CarResponseDto> create(@Valid @RequestBody CarRequestDto dto) {
        CarResponseDto created = carService.create(dto);
        return ResponseEntity.created(URI.create("/cars/" + created.getId())).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CarResponseDto> update(@PathVariable Long id, @Valid @RequestBody CarRequestDto dto) {
        return ResponseEntity.ok(carService.update(id, dto));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<CarResponseDto> patch(@PathVariable Long id, @Valid @RequestBody CarUpdateDto dto) {
        return ResponseEntity.ok(carService.patch(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        carService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/document")
    public ResponseEntity<Map<String, String>> uploadDocument(@PathVariable Long id,
                                                              @RequestParam("file") MultipartFile file) {
        String path = carDocumentService.uploadDocument(id, file);
        return ResponseEntity.ok(Map.of("message", "Autopapieren geüpload.", "path", path));
    }

    @GetMapping("/{id}/document")
    public ResponseEntity<Resource> downloadDocument(@PathVariable Long id) {
        Resource resource = carDocumentService.downloadDocument(id);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"autopapieren-" + id + ".pdf\"")
                .body(resource);
    }
}
