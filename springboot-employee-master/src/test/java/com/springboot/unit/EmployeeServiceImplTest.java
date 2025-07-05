package com.springboot.unit;

import com.springboot.exception.EmployeeNotFoundException;
import com.springboot.model.Employee;
import com.springboot.repository.EmployeeRepository;
import com.springboot.service.EmployeeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EmployeeServiceImplTest {

    @InjectMocks
    private EmployeeServiceImpl employeeService;

    @Mock
    private EmployeeRepository employeeRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
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

        when(employeeRepository.save(employee)).thenReturn(savedEmployee);

        Employee result = employeeService.save(employee);

        assertEquals(savedEmployee, result);
        verify(employeeRepository).save(employee);
    }

    @Test
    void testGetAllEmployees() {
        Employee emp1 = new Employee();
        emp1.setId(1);
        emp1.setName("John Doe");

        Employee emp2 = new Employee();
        emp2.setId(2);
        emp2.setName("Jane Smith");

        List<Employee> employees = Arrays.asList(emp1, emp2);

        when(employeeRepository.findAll()).thenReturn(employees);

        List<Employee> result = employeeService.getAll();

        assertEquals(2, result.size());
        assertTrue(result.contains(emp1));
        assertTrue(result.contains(emp2));
        verify(employeeRepository).findAll();
    }

    @Test
    void testFindByIdFound() {
        Employee employee = new Employee();
        employee.setId(1);
        employee.setName("John Doe");

        when(employeeRepository.findById(1)).thenReturn(Optional.of(employee));

        Optional<Employee> result = employeeService.findById(1);

        assertTrue(result.isPresent());
        assertEquals(employee, result.get());
        verify(employeeRepository).findById(1);
    }

    @Test
    void testFindByIdNotFound() {
        when(employeeRepository.findById(99)).thenReturn(Optional.empty());

        EmployeeNotFoundException exception = assertThrows(EmployeeNotFoundException.class, () -> {
            employeeService.findById(99);
        });

        assertTrue(exception.getMessage().contains("Employee"));
        assertTrue(exception.getMessage().contains("Id"));
        assertTrue(exception.getMessage().contains("99"));

        verify(employeeRepository).findById(99);
    }

    @Test
    void testUpdateEmployeeFound() {
        Employee existingEmployee = new Employee();
        existingEmployee.setId(1);
        existingEmployee.setName("Old Name");
        existingEmployee.setEmail("old@example.com");
        existingEmployee.setRole("User");

        Employee updateInfo = new Employee();
        updateInfo.setName("New Name");
        updateInfo.setEmail("new@example.com");
        updateInfo.setRole("Admin");

        Employee savedEmployee = new Employee();
        savedEmployee.setId(1);
        savedEmployee.setName("New Name");
        savedEmployee.setEmail("new@example.com");
        savedEmployee.setRole("Admin");

        when(employeeRepository.findById(1)).thenReturn(Optional.of(existingEmployee));
        when(employeeRepository.save(existingEmployee)).thenReturn(savedEmployee);

        Employee result = employeeService.update(updateInfo, 1);

        assertEquals(savedEmployee, result);
        assertEquals("New Name", existingEmployee.getName());
        assertEquals("new@example.com", existingEmployee.getEmail());
        assertEquals("Admin", existingEmployee.getRole());

        verify(employeeRepository).findById(1);
        verify(employeeRepository).save(existingEmployee);
    }

    @Test
    void testUpdateEmployeeNotFound() {
        Employee updateInfo = new Employee();
        updateInfo.setName("New Name");
        updateInfo.setEmail("new@example.com");

        when(employeeRepository.findById(99)).thenReturn(Optional.empty());

        EmployeeNotFoundException exception = assertThrows(EmployeeNotFoundException.class, () -> {
            employeeService.update(updateInfo, 99);
        });

        assertTrue(exception.getMessage().contains("Employee"));
        assertTrue(exception.getMessage().contains("Id"));
        assertTrue(exception.getMessage().contains("99"));

        verify(employeeRepository).findById(99);
        verify(employeeRepository, never()).save(any());
    }

    @Test
    void testDeleteEmployeeFound() {
        Employee existingEmployee = new Employee();
        existingEmployee.setId(1);
        existingEmployee.setName("To Delete");

        when(employeeRepository.findById(1)).thenReturn(Optional.of(existingEmployee));
        doNothing().when(employeeRepository).delete(existingEmployee);

        assertDoesNotThrow(() -> employeeService.delete(1));

        verify(employeeRepository).findById(1);
        verify(employeeRepository).delete(existingEmployee);
    }

    @Test
    void testDeleteEmployeeNotFound() {
        when(employeeRepository.findById(99)).thenReturn(Optional.empty());

        EmployeeNotFoundException exception = assertThrows(EmployeeNotFoundException.class, () -> {
            employeeService.delete(99);
        });

        assertTrue(exception.getMessage().contains("Employee"));
        assertTrue(exception.getMessage().contains("Id"));
        assertTrue(exception.getMessage().contains("99"));

        verify(employeeRepository).findById(99);
        verify(employeeRepository, never()).delete(any());
    }
}
