package nl.automaat.api.dto;

import lombok.Getter;
import lombok.Setter;
import nl.automaat.api.model.RepairStatus;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class RepairRequestDto {
    private Long carId;
    private LocalDate repairDate;
    private RepairStatus status;
    private String notes;
    private List<Long> partIds = new ArrayList<>();
}
