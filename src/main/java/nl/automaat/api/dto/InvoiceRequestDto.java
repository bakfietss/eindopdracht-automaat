package nl.automaat.api.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import nl.automaat.api.model.PaymentStatus;

@Getter
@Setter
public class InvoiceRequestDto {

    @NotNull(message = "Reparatie-id is verplicht.")
    private Long repairId;

    // optioneel, default pending
    private PaymentStatus paymentStatus;
}
