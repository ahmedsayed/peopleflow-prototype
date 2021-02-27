package com.pplflw.prototype.domains;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.pplflw.prototype.config.constants.JpaConstants;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "employees_contracts")
public class EmployeeContract extends Auditable {

    @Id
    @GeneratedValue(generator = "employee_contract_generator")
    @SequenceGenerator(
            name = "employee_contract_generator",
            sequenceName = "employee_contract_sequence")
    private Long id;

    @Column(name = "start_date", nullable = false, updatable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = JpaConstants.DATA_TIME_FORMAT)
    private LocalDateTime startDate;

    @Column(name = "end_date", nullable = false, updatable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = JpaConstants.DATA_TIME_FORMAT)
    private LocalDateTime endDate;

    @Column(name = "salary", nullable = false)
    private BigDecimal salary;

    @Column(name = "active", nullable = false)
    private boolean active;

    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Column(name = "employer_id", nullable = false)
    private Long employerId;

    private EmployeeContract() {
    }

    public EmployeeContract(LocalDateTime startDate, LocalDateTime endDate, BigDecimal salary, boolean active,
                            Employee employee, Long employerId) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.salary = salary;
        this.active = active;
        this.employee = employee;
        this.employerId = employerId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public BigDecimal getSalary() {
        return salary;
    }

    public void setSalary(BigDecimal salary) {
        this.salary = salary;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public Long getEmployerId() {
        return employerId;
    }

    public void setEmployerId(Long employerId) {
        this.employerId = employerId;
    }
}
