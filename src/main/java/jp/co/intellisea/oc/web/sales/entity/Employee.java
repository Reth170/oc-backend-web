package jp.co.intellisea.oc.web.sales.entity;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.Size;

@Data
@Entity
@Table(name = "employee")
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "employee_id", nullable = false)
    private Integer employeeId;

    @Size(max = 255)
    @Column(name = "name")
    private String name;

    @Size(max = 255)
    @Column(name = "mail")
    private String mail;

    @Column(name = "employee_code")
    private Integer employeeCode;

    @Size(max = 255)
    @Column(name = "duty")
    private String duty;
}