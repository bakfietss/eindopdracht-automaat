package nl.automaat.api.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class PartResponseDto {
    private Long id;
    private String name;
    private BigDecimal price;
    private Integer stockQuantity;
}
