package nl.automaat.api.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomerResponseDto {
    private Long id;
    private String name;
    private String phoneNumber;
    private String email;
}
