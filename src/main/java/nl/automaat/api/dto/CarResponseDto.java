package nl.automaat.api.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CarResponseDto {
    private Long id;
    private String licensePlate;
    private String brand;
    private String model;
    private Integer buildYear;
    private Long customerId;
}
