package com.pplflw.prototype.services.impl;

import com.pplflw.prototype.config.constants.KafkaConstants;
import com.pplflw.prototype.domains.Employee;
import com.pplflw.prototype.domains.EmployeeContract;
import com.pplflw.prototype.domains.Employer;
import com.pplflw.prototype.domains.enums.EmployeeStatus;
import com.pplflw.prototype.domains.enums.EmployeeStatusEvent;
import com.pplflw.prototype.exceptions.BusinessException;
import com.pplflw.prototype.exceptions.ResourceNotFoundException;
import com.pplflw.prototype.repositories.EmployeesRepository;
import com.pplflw.prototype.repositories.EmployersRepository;
import com.pplflw.prototype.services.EmployeesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class EmployeesServiceImpl implements EmployeesService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    
    private final EmployeesRepository employeesRepository;
    private final EmployersRepository employersRepository;
    private final KafkaTemplate<String, Employee> employeeKafkaTemplate;
    private final StateMachineFactory<EmployeeStatus, EmployeeStatusEvent> stateMachineFactory;
    
    private final EmployeeStatus initialEmployeeStatus;
    
    @Autowired
    public EmployeesServiceImpl(EmployeesRepository employeesRepository,
                                EmployersRepository employersRepository,
                                KafkaTemplate<String, Employee> employeeKafkaTemplate,
                                @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection") 
                                StateMachineFactory<EmployeeStatus, EmployeeStatusEvent> stateMachineFactory) {
        this.employeesRepository = employeesRepository;
        this.employersRepository = employersRepository;
        this.employeeKafkaTemplate = employeeKafkaTemplate;
        this.stateMachineFactory = stateMachineFactory;
        
        initialEmployeeStatus = stateMachineFactory.getStateMachine().getInitialState().getId();
    }

    @Override
    public Employee addEmployee(Employee employee) throws BusinessException {
        
        validateNewEmployee(employee);

        employee.setStatus(initialEmployeeStatus);

        Employee savedEmployee = saveAndSendEmployee(employee);

        return savedEmployee;
    }

    @Override
    public Employee updateEmployeeStatus(Long employeeId, EmployeeStatusEvent event) throws BusinessException {
        Optional<Employee> fetchedEmployee = this.employeesRepository.findById(employeeId);

        if (fetchedEmployee.isPresent()) {
            Employee employee = fetchedEmployee.get();

            StateMachine<EmployeeStatus, EmployeeStatusEvent> machine = initEmployeeStatus(employee.getId(), employee.getStatus());

            boolean accepted = machine.sendEvent(event);
            if (!accepted) {
                throw new BusinessException("employee", "status", event, "This transition isn't allowed.");
            }

            employee.setStatus(machine.getState().getId());

            Employee savedEmployee = saveAndSendEmployee(employee);
            return savedEmployee;
        } else {
            throw new ResourceNotFoundException("Record not found with id: " + employeeId);
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

    private Employee saveAndSendEmployee(Employee employee) {
        Employee savedEmployee = employeesRepository.save(employee);

        if(savedEmployee.getId() != null) {
            employeeKafkaTemplate.send(KafkaConstants.EMPLOYEES_TOPIC_NAME, savedEmployee.getId().toString(), savedEmployee);
        }
        return savedEmployee;
    }
    
    private StateMachine<EmployeeStatus, EmployeeStatusEvent> initEmployeeStatus(Long employeeId, EmployeeStatus employeeStatus) {
        StateMachine<EmployeeStatus, EmployeeStatusEvent> machine = stateMachineFactory.getStateMachine(String.valueOf(employeeId));
        machine.stop();
        
        machine.getStateMachineAccessor().doWithAllRegions(function -> {
            function.resetStateMachine(new DefaultStateMachineContext<>(
                    employeeStatus, null, null, null
            ));
        });
        
        machine.start();
        return machine;
    }
}
