package nl.automaat.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class PartRequestDto {

    @NotBlank(message = "Naam is verplicht.")
    private String name;

    @NotNull(message = "Prijs is verplicht.")
    private BigDecimal price;

    @NotNull(message = "Voorraad is verplicht.")
    private Integer stockQuantity;
}
