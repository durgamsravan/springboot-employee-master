package com.springboot.service;

import com.springboot.model.Employee;

import java.util.List;
import java.util.Optional;

public interface EmployeeService {
    Employee save(Employee employee);

    List<Employee> getAll();

    Optional<Employee> findById(Integer id);

    Employee update(Employee employee, Integer id);

    void delete(Integer id);
}