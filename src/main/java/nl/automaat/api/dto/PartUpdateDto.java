package nl.automaat.api.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class PartUpdateDto {
    private String name;

    @DecimalMin(value = "0.0", message = "Prijs mag niet negatief zijn.")
    private BigDecimal price;

    @Min(value = 0, message = "Voorraad mag niet negatief zijn.")
    private Integer stockQuantity;
}
