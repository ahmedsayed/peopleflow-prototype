package com.pplflw.prototype.services;

import com.pplflw.prototype.domains.Employee;
import com.pplflw.prototype.domains.enums.EmployeeStatusEvent;
import com.pplflw.prototype.exceptions.BusinessException;

import java.util.List;

public interface EmployeesService {
    
    Employee addEmployee(Employee employee) throws BusinessException;

    Employee updateEmployeeStatus(Long employeeId, EmployeeStatusEvent event) throws BusinessException;

    List<Employee> findEmployees();

    Employee findEmployee(long employeeId);
}
