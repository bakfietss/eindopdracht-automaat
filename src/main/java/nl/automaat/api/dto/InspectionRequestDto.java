package nl.automaat.api.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import nl.automaat.api.model.InspectionStatus;

import java.time.LocalDate;

@Getter
@Setter
public class InspectionRequestDto {

    @NotNull(message = "Auto-id is verplicht.")
    private Long carId;

    @NotNull(message = "Keuringsdatum is verplicht.")
    private LocalDate inspectionDate;

    @NotNull(message = "Status is verplicht (SCHEDULED, IN_PROGRESS, APPROVED, REJECTED).")
    private InspectionStatus status;

    private String issues;
}
