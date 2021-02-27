package com.pplflw.prototype.services.impl;

import com.pplflw.prototype.config.constants.KafkaConstants;
import com.pplflw.prototype.domains.Employee;
import com.pplflw.prototype.domains.EmployeeContract;
import com.pplflw.prototype.domains.Employer;
import com.pplflw.prototype.exceptions.BusinessException;
import com.pplflw.prototype.exceptions.ResourceNotFoundException;
import com.pplflw.prototype.repositories.EmployeesRepository;
import com.pplflw.prototype.repositories.EmployersRepository;
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
    private final EmployersRepository employersRepository;
    private final KafkaTemplate<String, Employee> employeeKafkaTemplate;

    @Autowired
    public EmployeesServiceImpl(EmployeesRepository employeesRepository,
                                EmployersRepository employersRepository,
                                KafkaTemplate<String, Employee> employeeKafkaTemplate) {
        this.employeesRepository = employeesRepository;
        this.employersRepository = employersRepository;
        this.employeeKafkaTemplate = employeeKafkaTemplate;
    }

    @Override
    public Employee addEmployee(Employee employee) throws BusinessException {
        
        validateNewEmployee(employee);

        Employee savedEmployee = employeesRepository.save(employee);
        
        if(savedEmployee.getId() != null) {
            employeeKafkaTemplate.send(KafkaConstants.EMPLOYEES_TOPIC_NAME, employee.getId().toString(), employee);
        }
        
        return savedEmployee;
    }

    @Override
    public Employee updateEmployeeStatus(Employee employee) throws BusinessException {
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
    
    private void validateNewEmployee(Employee employee) throws BusinessException {
        
        if(employee.getContracts() != null) {
            if(employee.getContracts().size() == 1) {
                EmployeeContract contract = employee.getContracts().get(0);
                
                if(contract == null || contract.getEmployer() == null) {
                    throw new BusinessException("employeeContract", "employer", null, "Contract's Employer is missing.");
                } else {
                    Optional<Employer> savedEmployer = employersRepository.findById(contract.getEmployer().getId());
                
                    if(savedEmployer.isPresent() && savedEmployer.get().isActive()) {
                        contract.setEmployer(savedEmployer.get());
                    } else {
                        throw new BusinessException("employeeContract", "employer", null, "Invalid Contract's Employer.");
                    }
                }
            } else if(employee.getContracts().size() > 1) {
                throw new BusinessException("employee", "contracts", null, "New Employee cannot have more than one contract.");
            }
        }
        
        // Add any other validation here..
    }
}
