package nl.automaat.api.dto;

import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CarUpdateDto {
    private String licensePlate;
    private String brand;
    private String model;

    @Min(value = 1900, message = "Bouwjaar moet na 1900 zijn.")
    private Integer buildYear;

    private Long customerId;
}
