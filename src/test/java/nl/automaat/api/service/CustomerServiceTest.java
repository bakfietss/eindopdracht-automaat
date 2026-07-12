package nl.automaat.api.service;

import jakarta.persistence.EntityNotFoundException;
import nl.automaat.api.dto.CustomerRequestDto;
import nl.automaat.api.dto.CustomerResponseDto;
import nl.automaat.api.dto.CustomerUpdateDto;
import nl.automaat.api.model.Customer;
import nl.automaat.api.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerService customerService;

    private Customer customer;

    @BeforeEach
    void setUp() {
        customer = new Customer();
        customer.setId(1L);
        customer.setName("Henk de Vries");
        customer.setPhoneNumber("0612345678");
        customer.setEmail("henk@example.com");
    }

    @Test
    void getAll_returnsMappedDtos() {
        // Arrange
        when(customerRepository.findAll()).thenReturn(List.of(customer));
        // Act
        List<CustomerResponseDto> result = customerService.getAll();
        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getEmail()).isEqualTo("henk@example.com");
    }

    @Test
    void getById_existing_returnsDto() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        CustomerResponseDto result = customerService.getById(1L);
        assertThat(result.getName()).isEqualTo("Henk de Vries");
    }

    @Test
    void getById_missing_throwsNotFound() {
        when(customerRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> customerService.getById(99L))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void create_newEmail_savesAndReturnsDto() {
        CustomerRequestDto dto = new CustomerRequestDto();
        dto.setName("Anna");
        dto.setPhoneNumber("0600000000");
        dto.setEmail("anna@example.com");
        when(customerRepository.existsByEmail("anna@example.com")).thenReturn(false);
        when(customerRepository.save(any(Customer.class))).thenAnswer(i -> {
            Customer c = i.getArgument(0);
            c.setId(2L);
            return c;
        });

        CustomerResponseDto result = customerService.create(dto);

        assertThat(result.getId()).isEqualTo(2L);
        verify(customerRepository).save(any(Customer.class));
    }

    @Test
    void create_duplicateEmail_throws() {
        CustomerRequestDto dto = new CustomerRequestDto();
        dto.setEmail("dup@example.com");
        when(customerRepository.existsByEmail("dup@example.com")).thenReturn(true);

        assertThatThrownBy(() -> customerService.create(dto))
                .isInstanceOf(IllegalArgumentException.class);
        verify(customerRepository, never()).save(any());
    }

    @Test
    void update_changesFields() {
        CustomerRequestDto dto = new CustomerRequestDto();
        dto.setName("Henk Gewijzigd");
        dto.setPhoneNumber("0611111111");
        dto.setEmail("henk@example.com");
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(customerRepository.save(any(Customer.class))).thenAnswer(i -> i.getArgument(0));

        CustomerResponseDto result = customerService.update(1L, dto);

        assertThat(result.getName()).isEqualTo("Henk Gewijzigd");
    }

    @Test
    void update_toExistingOtherEmail_throws() {
        CustomerRequestDto dto = new CustomerRequestDto();
        dto.setEmail("taken@example.com");
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(customerRepository.existsByEmail("taken@example.com")).thenReturn(true);

        assertThatThrownBy(() -> customerService.update(1L, dto))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void patch_onlyUpdatesProvidedFields() {
        CustomerUpdateDto dto = new CustomerUpdateDto();
        dto.setName("Nieuwe Naam");
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(customerRepository.save(any(Customer.class))).thenAnswer(i -> i.getArgument(0));

        CustomerResponseDto result = customerService.patch(1L, dto);

        assertThat(result.getName()).isEqualTo("Nieuwe Naam");
        assertThat(result.getPhoneNumber()).isEqualTo("0612345678");
        assertThat(result.getEmail()).isEqualTo("henk@example.com");
    }

    @Test
    void delete_existing_deletes() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        customerService.delete(1L);
        verify(customerRepository).delete(customer);
    }
}
