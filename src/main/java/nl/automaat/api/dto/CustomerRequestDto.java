package nl.automaat.api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomerRequestDto {

    @NotBlank(message = "Naam is verplicht.")
    private String name;

    @NotBlank(message = "Telefoonnummer is verplicht.")
    private String phoneNumber;

    @NotBlank(message = "E-mailadres is verplicht.")
    private String email;
}
