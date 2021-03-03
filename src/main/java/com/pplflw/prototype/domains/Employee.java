package com.pplflw.prototype.domains;

import com.pplflw.prototype.domains.enums.EmployeeStatus;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "employees")
public class Employee extends Auditable {

    @Id
    @GeneratedValue(generator = "employee_generator")
    @SequenceGenerator(name = "employee_generator", sequenceName = "employee_sequence", allocationSize = 1)
    private Long id;
    
    @Column(name = "first_name")
    private String firstName;
    
    @Column(name = "last_name")
    private String lastName;
    
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private EmployeeStatus status;
    
    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL)
    private List<EmployeeContract> contracts;
    
    protected Employee() {
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

    public List<EmployeeContract> getContracts() {
        return contracts;
    }

    public void setContracts(List<EmployeeContract> contracts) {
        this.contracts = contracts;
        if(this.contracts != null) {
            for(EmployeeContract contract : this.contracts) {
                contract.setEmployee(this);
            }
        }
    }

    public EmployeeStatus getStatus() {
        return status;
    }

    public void setStatus(EmployeeStatus status) {
        this.status = status;
    }
}
