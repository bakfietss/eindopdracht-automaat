package nl.automaat.api.controller;

import nl.automaat.api.dto.CustomerRequestDto;
import nl.automaat.api.dto.CustomerResponseDto;
import nl.automaat.api.service.CustomerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping
    public ResponseEntity<List<CustomerResponseDto>> getAll() {
        return ResponseEntity.ok(customerService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(customerService.getById(id));
    }

    @PostMapping
    public ResponseEntity<CustomerResponseDto> create(@RequestBody CustomerRequestDto dto) {
        CustomerResponseDto created = customerService.create(dto);
        // 201 Created + Location header naar de nieuwe klant
        return ResponseEntity.created(URI.create("/customers/" + created.getId())).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomerResponseDto> update(@PathVariable Long id, @RequestBody CustomerRequestDto dto) {
        return ResponseEntity.ok(customerService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        customerService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
