package com.springboot.controller;

import com.springboot.exception.EmployeeNotFoundException;
import com.springboot.model.Employee;
import com.springboot.service.EmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private static final Logger logger = LoggerFactory.getLogger(EmployeeController.class);

    @Autowired
    private EmployeeService employeeService;

    @PostMapping("/create")
    public ResponseEntity<Employee> save(@RequestBody Employee employee) {
        Employee savedEmployee = employeeService.save(employee);
        logger.info("Creating an employee with details: ID={}, Name={}, Email={}", savedEmployee.getId(), savedEmployee.getName(), savedEmployee.getEmail());
        return new ResponseEntity<>(savedEmployee, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Optional<Employee>> getById(@PathVariable Integer id) {
        Optional<Employee> employee = employeeService.findById(id);
        employee.ifPresent(e -> logger.info("Retrieved employee details: ID={}, Name={}, Email={}", e.getId(), e.getName(), e.getEmail()));
        return ResponseEntity.ok(employee);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Employee> update(@PathVariable Integer id, @RequestBody Employee employee) {
        logger.info("Updating employee with ID={}, New Details: Name={}, Email={}", id, employee.getName(), employee.getEmail());
        return ResponseEntity.ok(employeeService.update(employee, id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Integer id) {
        Employee employee = employeeService.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee", "Id", id));
        logger.info("Deleting employee with details: ID={}, Name={}, Email={}", employee.getId(), employee.getName(), employee.getEmail());
        employeeService.delete(id);
        return ResponseEntity.ok("Employee deleted successfully");
    }
}