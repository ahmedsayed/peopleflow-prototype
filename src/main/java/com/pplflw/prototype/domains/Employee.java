package com.pplflw.prototype.domains;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.pplflw.prototype.config.constants.JpaConstants;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "employees")
public class Employee extends Auditable {

    @Id
    @GeneratedValue(generator = "employee_generator")
    @SequenceGenerator(
            name = "employee_generator",
            sequenceName = "employee_sequence")
    private Long id;
    
    @Column(name = "first_name")
    private String firstName;
    
    @Column(name = "last_name")
    private String lastName;
    
    @Column(name = "status")
    private EmployeeStatus status;
    
    @OneToMany(mappedBy = "employee")
    private List<EmployeeContract> contracts;

    public Employee() {
    }

    public Employee(String firstName, String lastName, EmployeeStatus status) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
