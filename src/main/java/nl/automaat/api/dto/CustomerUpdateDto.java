package nl.automaat.api.dto;

import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomerUpdateDto {
    private String name;
    private String phoneNumber;

    @Email(message = "E-mailadres is ongeldig.")
    private String email;
}
