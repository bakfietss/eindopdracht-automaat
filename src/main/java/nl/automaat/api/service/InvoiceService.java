package nl.automaat.api.service;

import jakarta.persistence.EntityNotFoundException;
import nl.automaat.api.dto.InvoiceRequestDto;
import nl.automaat.api.dto.InvoiceResponseDto;
import nl.automaat.api.dto.InvoiceUpdateDto;
import nl.automaat.api.model.Invoice;
import nl.automaat.api.model.PaymentStatus;
import nl.automaat.api.model.Part;
import nl.automaat.api.model.Repair;
import nl.automaat.api.repository.InvoiceRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class InvoiceService {

    // btw 21%
    private static final BigDecimal VAT_RATE = new BigDecimal("0.21");

    private final InvoiceRepository invoiceRepository;
    private final RepairService repairService;

    public InvoiceService(InvoiceRepository invoiceRepository, RepairService repairService) {
        this.invoiceRepository = invoiceRepository;
        this.repairService = repairService;
    }

    public List<InvoiceResponseDto> getAll() {
        return invoiceRepository.findAll().stream().map(this::toDto).toList();
    }

    public InvoiceResponseDto getById(Long id) {
        return toDto(findOrThrow(id));
    }

    public InvoiceResponseDto create(InvoiceRequestDto dto) {
        if (invoiceRepository.existsByRepairId(dto.getRepairId())) {
            throw new IllegalStateException("Voor deze reparatie bestaat al een bon.");
        }
        Repair repair = repairService.findOrThrow(dto.getRepairId());

        // netto = som van de onderdelen, btw erbovenop
        BigDecimal net = repair.getParts().stream()
                .map(Part::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal vat = net.multiply(VAT_RATE).setScale(2, RoundingMode.HALF_UP);
        BigDecimal total = net.add(vat).setScale(2, RoundingMode.HALF_UP);

        Invoice invoice = new Invoice();
        invoice.setRepair(repair);
        invoice.setVatAmount(vat);
        invoice.setTotalAmount(total);
        invoice.setPaymentStatus(dto.getPaymentStatus() != null
                ? dto.getPaymentStatus() : PaymentStatus.PENDING);

        return toDto(invoiceRepository.save(invoice));
    }

    // alleen betaalstatus is aanpasbaar, bedragen liggen vast
    public InvoiceResponseDto update(Long id, InvoiceRequestDto dto) {
        Invoice invoice = findOrThrow(id);
        if (dto.getPaymentStatus() != null) {
            invoice.setPaymentStatus(dto.getPaymentStatus());
        }
        return toDto(invoiceRepository.save(invoice));
    }

    public InvoiceResponseDto patch(Long id, InvoiceUpdateDto dto) {
        Invoice invoice = findOrThrow(id);
        if (dto.getPaymentStatus() != null) {
            invoice.setPaymentStatus(dto.getPaymentStatus());
        }
        return toDto(invoiceRepository.save(invoice));
    }

    public void delete(Long id) {
        invoiceRepository.delete(findOrThrow(id));
    }

    public Invoice findOrThrow(Long id) {
        return invoiceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Bon met id " + id + " niet gevonden."));
    }

    private InvoiceResponseDto toDto(Invoice invoice) {
        InvoiceResponseDto dto = new InvoiceResponseDto();
        dto.setId(invoice.getId());
        dto.setRepairId(invoice.getRepair() != null ? invoice.getRepair().getId() : null);
        dto.setTotalAmount(invoice.getTotalAmount());
        dto.setVatAmount(invoice.getVatAmount());
        dto.setPaymentStatus(invoice.getPaymentStatus());
        return dto;
    }
}
