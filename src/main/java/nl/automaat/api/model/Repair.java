package nl.automaat.api.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "repairs")
@Getter
@Setter
@NoArgsConstructor
public class Repair {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "car_id", nullable = false)
    private Car car;

    @Column(name = "repair_date", nullable = false)
    private LocalDate repairDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RepairStatus status;

    @Column(columnDefinition = "TEXT")
    private String notes;
}
