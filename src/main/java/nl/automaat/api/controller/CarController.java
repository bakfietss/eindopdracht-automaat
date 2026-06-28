package nl.automaat.api.controller;

import nl.automaat.api.dto.CarRequestDto;
import nl.automaat.api.dto.CarResponseDto;
import nl.automaat.api.service.CarService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/cars")
public class CarController {

    private final CarService carService;

    public CarController(CarService carService) {
        this.carService = carService;
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
    public ResponseEntity<CarResponseDto> create(@RequestBody CarRequestDto dto) {
        CarResponseDto created = carService.create(dto);
        return ResponseEntity.created(URI.create("/cars/" + created.getId())).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CarResponseDto> update(@PathVariable Long id, @RequestBody CarRequestDto dto) {
        return ResponseEntity.ok(carService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        carService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
