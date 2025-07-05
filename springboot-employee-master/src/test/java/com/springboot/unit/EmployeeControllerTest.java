package com.springboot.unit;

import com.springboot.controller.EmployeeController;
import com.springboot.exception.EmployeeNotFoundException;
import com.springboot.model.Employee;
import com.springboot.service.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EmployeeControllerTest {

    @InjectMocks
    private EmployeeController employeeController;

    @Mock
    private EmployeeService employeeService;

    // Optional: mock logger if you want to verify logging (not mandatory)
    @Mock
    private Logger logger;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Inject mocked logger if needed (reflection or setter injection)
        // But usually logging is not tested in unit tests
    }

    @Test
    void testSaveEmployee() {
        Employee employee = new Employee();
        employee.setName("John Doe");
        employee.setEmail("john@example.com");

        Employee savedEmployee = new Employee();
        savedEmployee.setId(1);
        savedEmployee.setName("John Doe");
        savedEmployee.setEmail("john@example.com");

        when(employeeService.save(employee)).thenReturn(savedEmployee);

        ResponseEntity<Employee> response = employeeController.save(employee);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(savedEmployee, response.getBody());

        verify(employeeService).save(employee);
    }

    @Test
    void testGetByIdFound() {
        Employee employee = new Employee();
        employee.setId(1);
        employee.setName("Jane Doe");
        employee.setEmail("jane@example.com");

        when(employeeService.findById(1)).thenReturn(Optional.of(employee));

        ResponseEntity<Optional<Employee>> response = employeeController.getById(1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isPresent());
        assertEquals(employee, response.getBody().get());

        verify(employeeService).findById(1);
    }

    @Test
    void testGetByIdNotFound() {
        when(employeeService.findById(2)).thenReturn(Optional.empty());

        ResponseEntity<Optional<Employee>> response = employeeController.getById(2);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertFalse(response.getBody().isPresent());

        verify(employeeService).findById(2);
    }

    @Test
    void testUpdateEmployee() {
        Employee employeeUpdate = new Employee();
        employeeUpdate.setName("Updated Name");
        employeeUpdate.setEmail("updated@example.com");

        Employee updatedEmployee = new Employee();
        updatedEmployee.setId(1);
        updatedEmployee.setName("Updated Name");
        updatedEmployee.setEmail("updated@example.com");

        when(employeeService.update(employeeUpdate, 1)).thenReturn(updatedEmployee);

        ResponseEntity<Employee> response = employeeController.update(1, employeeUpdate);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedEmployee, response.getBody());

        verify(employeeService).update(employeeUpdate, 1);
    }

    @Test
    void testDeleteEmployeeFound() {
        Employee employee = new Employee();
        employee.setId(1);
        employee.setName("To Delete");
        employee.setEmail("delete@example.com");

        when(employeeService.findById(1)).thenReturn(Optional.of(employee));
        doNothing().when(employeeService).delete(1);

        ResponseEntity<String> response = employeeController.delete(1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Employee deleted successfully", response.getBody());

        verify(employeeService).findById(1);
        verify(employeeService).delete(1);
    }

    @Test
    void testDeleteEmployeeNotFound() {
        when(employeeService.findById(99)).thenReturn(Optional.empty());

        EmployeeNotFoundException thrown = assertThrows(EmployeeNotFoundException.class, () -> {
            employeeController.delete(99);
        });

        assertTrue(thrown.getMessage().contains("Employee"));
        assertTrue(thrown.getMessage().contains("Id"));
        assertTrue(thrown.getMessage().contains("99"));

        verify(employeeService).findById(99);
        verify(employeeService, never()).delete(anyInt());
    }
}
