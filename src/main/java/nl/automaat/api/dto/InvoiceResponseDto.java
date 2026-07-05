package nl.automaat.api.dto;

import lombok.Getter;
import lombok.Setter;
import nl.automaat.api.model.PaymentStatus;

import java.math.BigDecimal;

@Getter
@Setter
public class InvoiceResponseDto {
    private Long id;
    private Long repairId;
    private BigDecimal totalAmount;
    private BigDecimal vatAmount;
    private PaymentStatus paymentStatus;
}
