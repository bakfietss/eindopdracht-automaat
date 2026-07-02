package nl.automaat.api.dto;

import lombok.Getter;
import lombok.Setter;
import nl.automaat.api.model.RepairStatus;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class RepairResponseDto {
    private Long id;
    private Long carId;
    private LocalDate repairDate;
    private RepairStatus status;
    private String notes;
    private List<PartResponseDto> parts;
}
