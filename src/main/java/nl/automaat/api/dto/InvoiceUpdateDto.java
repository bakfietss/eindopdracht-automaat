package nl.automaat.api.dto;

import lombok.Getter;
import lombok.Setter;
import nl.automaat.api.model.PaymentStatus;

@Getter
@Setter
public class InvoiceUpdateDto {
    private PaymentStatus paymentStatus;
}
