package com.pplflw.prototype.web.rest;

import com.pplflw.prototype.domains.Employee;
import com.pplflw.prototype.exceptions.BusinessException;
import com.pplflw.prototype.services.EmployeesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
public class EmployeesController {

    private final EmployeesService employeesService;

    @Autowired
    public EmployeesController(EmployeesService employeesService) {
        this.employeesService = employeesService;
    }

    //TODO handle exceptions
    @GetMapping("/all")
    public ResponseEntity<List<Employee>> findAll() {
        return ResponseEntity.ok().body(employeesService.findEmployees());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Employee> findEmployee(@PathVariable(value = "id") long id) {
        return ResponseEntity.ok().body(employeesService.findEmployee(id));
    }

    @PostMapping("/add")
    public ResponseEntity<Employee> addEmployee(@Validated @RequestBody Employee employee) throws BusinessException {
        return ResponseEntity.ok().body(employeesService.addEmployee(employee));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Employee> updateEmployee(@PathVariable long id, @RequestBody Employee employee) 
            throws BusinessException {
        employee.setId(id);
        return ResponseEntity.ok().body(this.employeesService.updateEmployeeStatus(employee));
    }
}
