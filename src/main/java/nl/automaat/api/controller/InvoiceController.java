package nl.automaat.api.controller;

import jakarta.validation.Valid;
import nl.automaat.api.dto.InvoiceRequestDto;
import nl.automaat.api.dto.InvoiceResponseDto;
import nl.automaat.api.dto.InvoiceUpdateDto;
import nl.automaat.api.service.InvoicePdfService;
import nl.automaat.api.service.InvoiceService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/invoices")
public class InvoiceController {

    private final InvoiceService invoiceService;
    private final InvoicePdfService invoicePdfService;

    public InvoiceController(InvoiceService invoiceService, InvoicePdfService invoicePdfService) {
        this.invoiceService = invoiceService;
        this.invoicePdfService = invoicePdfService;
    }

    @GetMapping
    public ResponseEntity<List<InvoiceResponseDto>> getAll() {
        return ResponseEntity.ok(invoiceService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<InvoiceResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(invoiceService.getById(id));
    }

    @PostMapping
    public ResponseEntity<InvoiceResponseDto> create(@Valid @RequestBody InvoiceRequestDto dto) {
        InvoiceResponseDto created = invoiceService.create(dto);
        return ResponseEntity.created(URI.create("/invoices/" + created.getId())).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<InvoiceResponseDto> update(@PathVariable Long id, @Valid @RequestBody InvoiceRequestDto dto) {
        return ResponseEntity.ok(invoiceService.update(id, dto));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<InvoiceResponseDto> patch(@PathVariable Long id, @Valid @RequestBody InvoiceUpdateDto dto) {
        return ResponseEntity.ok(invoiceService.patch(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        invoiceService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> downloadPdf(@PathVariable Long id) {
        byte[] pdf = invoicePdfService.generatePdf(id);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"bon-" + id + ".pdf\"")
                .body(pdf);
    }
}
