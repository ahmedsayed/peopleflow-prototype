package com.pplflw.prototype.services;

import com.pplflw.prototype.domains.Employee;

import java.util.List;

public interface EmployeesService {
    
    Employee addEmployee(Employee employee);

    Employee updateEmployee(Employee employee);

    List<Employee> findEmployees();

    Employee findEmployee(long employeeId);
}
