package com.pplflw.prototype.repositories;

import com.pplflw.prototype.domains.EmployeeContract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeesContractsRepository extends JpaRepository<EmployeeContract, Long> {
}
