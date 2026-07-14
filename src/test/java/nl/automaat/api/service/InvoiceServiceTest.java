package nl.automaat.api.service;

import jakarta.persistence.EntityNotFoundException;
import nl.automaat.api.dto.InvoiceRequestDto;
import nl.automaat.api.dto.InvoiceResponseDto;
import nl.automaat.api.dto.InvoiceUpdateDto;
import nl.automaat.api.model.Invoice;
import nl.automaat.api.model.Part;
import nl.automaat.api.model.PaymentStatus;
import nl.automaat.api.model.Repair;
import nl.automaat.api.repository.InvoiceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InvoiceServiceTest {

    @Mock
    private InvoiceRepository invoiceRepository;
    @Mock
    private RepairService repairService;
    @InjectMocks
    private InvoiceService invoiceService;

    private Repair repair;

    @BeforeEach
    void setUp() {
        Part frontBrake = new Part();
        frontBrake.setName("Front brake pad set");
        frontBrake.setPrice(new BigDecimal("45.50"));
        Part rearBrake = new Part();
        rearBrake.setName("Rear brake pad set");
        rearBrake.setPrice(new BigDecimal("39.95"));

        repair = new Repair();
        repair.setId(1L);
        repair.setParts(List.of(frontBrake, rearBrake));
    }

    @Test
    void create_computesVatAndTotalCorrectly() {
        // Arrange — netto 85.45, BTW 21% = 17.94, totaal 103.39
        InvoiceRequestDto dto = new InvoiceRequestDto();
        dto.setRepairId(1L);
        when(invoiceRepository.existsByRepairId(1L)).thenReturn(false);
        when(repairService.findOrThrow(1L)).thenReturn(repair);
        when(invoiceRepository.save(any(Invoice.class))).thenAnswer(i -> {
            Invoice inv = i.getArgument(0);
            inv.setId(10L);
            return inv;
        });

        // Act
        InvoiceResponseDto result = invoiceService.create(dto);

        // Assert
        assertThat(result.getVatAmount()).isEqualByComparingTo("17.94");
        assertThat(result.getTotalAmount()).isEqualByComparingTo("103.39");
        assertThat(result.getPaymentStatus()).isEqualTo(PaymentStatus.PENDING);
    }

    @Test
    void create_whenInvoiceAlreadyExists_throwsConflict() {
        InvoiceRequestDto dto = new InvoiceRequestDto();
        dto.setRepairId(1L);
        when(invoiceRepository.existsByRepairId(1L)).thenReturn(true);

        assertThatThrownBy(() -> invoiceService.create(dto))
                .isInstanceOf(IllegalStateException.class);
        verify(invoiceRepository, never()).save(any());
    }

    @Test
    void create_respectsProvidedPaymentStatus() {
        InvoiceRequestDto dto = new InvoiceRequestDto();
        dto.setRepairId(1L);
        dto.setPaymentStatus(PaymentStatus.PAID);
        when(invoiceRepository.existsByRepairId(1L)).thenReturn(false);
        when(repairService.findOrThrow(1L)).thenReturn(repair);
        when(invoiceRepository.save(any(Invoice.class))).thenAnswer(i -> i.getArgument(0));

        InvoiceResponseDto result = invoiceService.create(dto);

        assertThat(result.getPaymentStatus()).isEqualTo(PaymentStatus.PAID);
    }

    @Test
    void update_changesPaymentStatus() {
        Invoice invoice = new Invoice();
        invoice.setId(5L);
        invoice.setPaymentStatus(PaymentStatus.PENDING);
        when(invoiceRepository.findById(5L)).thenReturn(Optional.of(invoice));
        when(invoiceRepository.save(any(Invoice.class))).thenAnswer(i -> i.getArgument(0));

        InvoiceRequestDto dto = new InvoiceRequestDto();
        dto.setPaymentStatus(PaymentStatus.PAID);
        InvoiceResponseDto result = invoiceService.update(5L, dto);

        assertThat(result.getPaymentStatus()).isEqualTo(PaymentStatus.PAID);
    }

    @Test
    void getById_missing_throwsNotFound() {
        when(invoiceRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> invoiceService.getById(99L))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void delete_existing_deletes() {
        Invoice invoice = new Invoice();
        invoice.setId(7L);
        when(invoiceRepository.findById(7L)).thenReturn(Optional.of(invoice));
        invoiceService.delete(7L);
        verify(invoiceRepository).delete(invoice);
    }

    @Test
    void getAll_returnsMappedList() {
        Invoice invoice = new Invoice();
        invoice.setId(1L);
        invoice.setRepair(repair);
        invoice.setTotalAmount(new BigDecimal("103.39"));
        invoice.setVatAmount(new BigDecimal("17.94"));
        invoice.setPaymentStatus(PaymentStatus.PAID);
        when(invoiceRepository.findAll()).thenReturn(List.of(invoice));

        List<InvoiceResponseDto> result = invoiceService.getAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getRepairId()).isEqualTo(1L);
    }

    @Test
    void getById_existing_returnsDto() {
        Invoice invoice = new Invoice();
        invoice.setId(3L);
        invoice.setRepair(repair);
        invoice.setTotalAmount(new BigDecimal("103.39"));
        invoice.setVatAmount(new BigDecimal("17.94"));
        invoice.setPaymentStatus(PaymentStatus.PENDING);
        when(invoiceRepository.findById(3L)).thenReturn(Optional.of(invoice));

        InvoiceResponseDto result = invoiceService.getById(3L);

        assertThat(result.getId()).isEqualTo(3L);
        assertThat(result.getRepairId()).isEqualTo(1L);
    }

    @Test
    void patch_changesPaymentStatus() {
        Invoice invoice = new Invoice();
        invoice.setId(8L);
        invoice.setPaymentStatus(PaymentStatus.PENDING);
        when(invoiceRepository.findById(8L)).thenReturn(Optional.of(invoice));
        when(invoiceRepository.save(any(Invoice.class))).thenAnswer(i -> i.getArgument(0));

        InvoiceUpdateDto dto = new InvoiceUpdateDto();
        dto.setPaymentStatus(PaymentStatus.PAID);
        InvoiceResponseDto result = invoiceService.patch(8L, dto);

        assertThat(result.getPaymentStatus()).isEqualTo(PaymentStatus.PAID);
    }
}
