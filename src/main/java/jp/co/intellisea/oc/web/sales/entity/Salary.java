package jp.co.intellisea.oc.web.sales.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Getter
@Setter
@Entity
@Table(name = "salary")
public class Salary {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @NotNull
    @Column(name = "year", nullable = false)
    private Short year;

    @NotNull
    @Column(name = "month", nullable = false)
    private Short month;

    @NotNull
    @Column(name = "payment_date", nullable = false)
    private LocalDate paymentDate;

    @NotNull
    @Column(name = "base_salary", nullable = false)
    private Integer baseSalary;

    @NotNull
    @Column(name = "overtime_allowance", nullable = false)
    private Integer overtimeAllowance;

    @NotNull
    @Column(name = "commuting_allowance", nullable = false)
    private Integer commutingAllowance;

    @NotNull
    @Column(name = "health_insurance", nullable = false)
    private Integer healthInsurance;

    @NotNull
    @Column(name = "pension", nullable = false)
    private Integer pension;

    @NotNull
    @Column(name = "employment_insurance", nullable = false)
    private Integer employmentInsurance;

    @NotNull
    @Column(name = "income_tax", nullable = false)
    private Integer incomeTax;

    @NotNull
    @Column(name = "resident_tax", nullable = false)
    private Integer residentTax;

    @NotNull
    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @NotNull
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

}