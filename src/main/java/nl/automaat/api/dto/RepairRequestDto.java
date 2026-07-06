package nl.automaat.api.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import nl.automaat.api.model.RepairStatus;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class RepairRequestDto {

    @NotNull(message = "Auto-id is verplicht.")
    private Long carId;

    @NotNull(message = "Reparatiedatum is verplicht.")
    private LocalDate repairDate;

    @NotNull(message = "Status is verplicht (OPEN, IN_PROGRESS, COMPLETED, CANCELLED).")
    private RepairStatus status;

    private String notes;

    // optioneel
    private List<Long> partIds = new ArrayList<>();
}
