package nl.automaat.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CarRequestDto {

    @NotBlank(message = "Kenteken is verplicht.")
    private String licensePlate;

    @NotBlank(message = "Merk is verplicht.")
    private String brand;

    @NotBlank(message = "Model is verplicht.")
    private String model;

    @NotNull(message = "Bouwjaar is verplicht.")
    private Integer buildYear;

    @NotNull(message = "Klant-id is verplicht.")
    private Long customerId;
}
