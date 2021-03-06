package com.pplflw.prototype.domains;

import com.pplflw.prototype.domains.enums.EmployeeStatus;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

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

    public Employee(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Employee employee = (Employee) o;

        return new EqualsBuilder()
                .append(firstName, employee.firstName)
                .append(lastName, employee.lastName)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(firstName)
                .append(lastName)
                .toHashCode();
    }
}
