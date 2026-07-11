package nl.automaat.api.service;

import jakarta.persistence.EntityNotFoundException;
import nl.automaat.api.dto.CustomerRequestDto;
import nl.automaat.api.dto.CustomerResponseDto;
import nl.automaat.api.dto.CustomerUpdateDto;
import nl.automaat.api.model.Customer;
import nl.automaat.api.repository.CustomerRepository;
import nl.automaat.api.util.PatchUtil;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public List<CustomerResponseDto> getAll() {
        return customerRepository.findAll().stream().map(this::toDto).toList();
    }

    public CustomerResponseDto getById(Long id) {
        return toDto(findOrThrow(id));
    }

    public CustomerResponseDto create(CustomerRequestDto dto) {
        // email moet uniek zijn
        if (customerRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Er bestaat al een klant met dit e-mailadres.");
        }
        Customer customer = new Customer();
        apply(customer, dto);
        return toDto(customerRepository.save(customer));
    }

    public CustomerResponseDto update(Long id, CustomerRequestDto dto) {
        Customer customer = findOrThrow(id);
        // alleen botsen als de email echt verandert naar een bestaande
        if (!customer.getEmail().equalsIgnoreCase(dto.getEmail())
                && customerRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Er bestaat al een klant met dit e-mailadres.");
        }
        apply(customer, dto);
        return toDto(customerRepository.save(customer));
    }

    public CustomerResponseDto patch(Long id, CustomerUpdateDto dto) {
        Customer customer = findOrThrow(id);
        if (dto.getEmail() != null
                && !customer.getEmail().equalsIgnoreCase(dto.getEmail())
                && customerRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Er bestaat al een klant met dit e-mailadres.");
        }
        PatchUtil.applyIfPresent(dto.getName(), customer::setName);
        PatchUtil.applyIfPresent(dto.getPhoneNumber(), customer::setPhoneNumber);
        PatchUtil.applyIfPresent(dto.getEmail(), customer::setEmail);
        return toDto(customerRepository.save(customer));
    }

    public void delete(Long id) {
        customerRepository.delete(findOrThrow(id));
    }

    private Customer findOrThrow(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Klant met id " + id + " niet gevonden."));
    }

    private void apply(Customer customer, CustomerRequestDto dto) {
        customer.setName(dto.getName());
        customer.setPhoneNumber(dto.getPhoneNumber());
        customer.setEmail(dto.getEmail());
    }

    private CustomerResponseDto toDto(Customer customer) {
        CustomerResponseDto dto = new CustomerResponseDto();
        dto.setId(customer.getId());
        dto.setName(customer.getName());
        dto.setPhoneNumber(customer.getPhoneNumber());
        dto.setEmail(customer.getEmail());
        return dto;
    }
}
