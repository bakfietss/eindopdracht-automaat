package nl.automaat.api.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomerRequestDto {
    private String name;
    private String phoneNumber;
    private String email;
}
