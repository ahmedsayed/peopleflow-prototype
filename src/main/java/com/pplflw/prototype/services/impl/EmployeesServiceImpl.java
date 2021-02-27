package com.pplflw.prototype.services.impl;

import com.pplflw.prototype.config.constants.KafkaConstants;
import com.pplflw.prototype.domains.Employee;
import com.pplflw.prototype.exceptions.ResourceNotFoundException;
import com.pplflw.prototype.repositories.EmployeesRepository;
import com.pplflw.prototype.services.EmployeesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class EmployeesServiceImpl implements EmployeesService {

    private final EmployeesRepository employeesRepository;
    private final KafkaTemplate<String, Employee> employeeKafkaTemplate;

    @Autowired
    public EmployeesServiceImpl(EmployeesRepository employeesRepository, KafkaTemplate<String, Employee> employeeKafkaTemplate) {
        this.employeesRepository = employeesRepository;
        this.employeeKafkaTemplate = employeeKafkaTemplate;
    }

    @Override
    public Employee addEmployee(Employee employee) {
        //TODO validate
        Employee savedEmployee = employeesRepository.save(employee);
        
        if(savedEmployee.getId() != null) {
            employeeKafkaTemplate.send(KafkaConstants.EMPLOYEES_TOPIC_NAME, employee.getId().toString(), employee);
        }
        
        return savedEmployee;
    }

    @Override
    public Employee updateEmployee(Employee employee) {
        Optional<Employee> exitingEmployee = this.employeesRepository.findById(employee.getId());

        if (exitingEmployee.isPresent()) {
            Employee updatedEmployee = exitingEmployee.get();
            
            //TODO validate
            updatedEmployee.setId(employee.getId());
            updatedEmployee.setFirstName(employee.getFirstName());
            updatedEmployee.setLastName(employee.getLastName());
            
            employeesRepository.save(updatedEmployee);
            return updatedEmployee;
        } else {
            throw new ResourceNotFoundException("Record not found with id: " + employee.getId());
        }
    }

    @Override
    public List<Employee> findEmployees() {
        return this.employeesRepository.findAll();
    }

    @Override
    public Employee findEmployee(long employeeId) {
        Optional<Employee> exitingEmployee = this.employeesRepository.findById(employeeId);

        if (exitingEmployee.isPresent()) {
            return exitingEmployee.get();
        } else {
            throw new ResourceNotFoundException("Record not found with id: " + employeeId);
        }
    }
}
