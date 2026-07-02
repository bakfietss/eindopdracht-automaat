package nl.automaat.api.dto;

import lombok.Getter;
import lombok.Setter;
import nl.automaat.api.model.InspectionStatus;

import java.time.LocalDate;

@Getter
@Setter
public class InspectionResponseDto {
    private Long id;
    private Long carId;
    private LocalDate inspectionDate;
    private InspectionStatus status;
    private String issues;
}
