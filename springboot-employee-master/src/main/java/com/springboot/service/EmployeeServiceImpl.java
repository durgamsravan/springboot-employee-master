package com.springboot.service;

import com.springboot.exception.EmployeeNotFoundException;
import com.springboot.model.Employee;
import com.springboot.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    public Employee save(Employee employee) {
        return employeeRepository.save(employee);
    }

    @Override
    public List<Employee> getAll() {
        return employeeRepository.findAll();
    }

    @Override
    public Optional<Employee> findById(Integer id) {
        return employeeRepository.findById(id)
                .map(Optional::of)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee", "Id", id));
    }

    @Override
    public Employee update(Employee employee, Integer id) {
        Employee existingEmployee = employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee", "Id", id));
        existingEmployee.setName(employee.getName());
        existingEmployee.setEmail(employee.getEmail());
        existingEmployee.setRole(employee.getRole());
        return employeeRepository.save(existingEmployee);
    }

    @Override
    public void delete(Integer id) {
        Employee existingEmployee = employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee", "Id", id));
        employeeRepository.delete(existingEmployee);
    }
}