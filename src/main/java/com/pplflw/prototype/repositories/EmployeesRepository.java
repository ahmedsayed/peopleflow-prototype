package com.pplflw.prototype.repositories;

import com.pplflw.prototype.domains.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeesRepository extends JpaRepository<Employee, Long> {
    
}
