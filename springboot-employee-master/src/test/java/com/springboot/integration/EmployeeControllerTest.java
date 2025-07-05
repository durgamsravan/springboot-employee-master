package com.springboot.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.model.Employee;
import com.springboot.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        employeeRepository.deleteAll();
    }

    @Test
    public void givenEmployeeObject_whenCreateEmployee_thenReturnCreatedEmployee() throws Exception {
        Employee employee = createEmployee();

        ResultActions response = mockMvc.perform(post("/api/employees/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(employee)));

        response.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name", is(employee.getName())))
                .andExpect(jsonPath("$.email", is(employee.getEmail())))
                .andExpect(jsonPath("$.role", is(employee.getRole())));
    }

    @Test
    public void givenEmployees_whenGetAllEmployees_thenReturnEmployeeList() throws Exception {
        List<Employee> employees = Arrays.asList(
                createEmployee(),
                Employee.builder().name("Arun").email("arun@gmail.com").role("Tester").build()
        );
        employeeRepository.saveAll(employees);

        ResultActions response = mockMvc.perform(get("/api/employees"));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(employees.size())))
                .andExpect(jsonPath("$[0].name", is("Ravi")))
                .andExpect(jsonPath("$[1].name", is("Arun")));
    }

    @Test
    public void givenEmployeeId_whenGetEmployeeById_thenReturnEmployee() throws Exception {
        Employee employee = createEmployee();
        Employee savedEmployee = employeeRepository.save(employee);

        ResultActions response = mockMvc.perform(get("/api/employees/{id}", savedEmployee.getId()));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(savedEmployee.getId())))
                .andExpect(jsonPath("$.name", is(savedEmployee.getName())))
                .andExpect(jsonPath("$.email", is(savedEmployee.getEmail())))
                .andExpect(jsonPath("$.role", is(savedEmployee.getRole())));
    }

    @Test
    public void givenInvalidEmployeeId_whenGetEmployeeById_thenReturnNotFound() throws Exception {
        int invalidId = 999;

        ResultActions response = mockMvc.perform(get("/api/employees/{id}", invalidId));

        response.andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void givenUpdatedEmployee_whenUpdateEmployee_thenReturnUpdatedEmployee() throws Exception {
        Employee employee = createEmployee();
        Employee savedEmployee = employeeRepository.save(employee);

        Employee updatedEmployee = Employee.builder()
                .name("Ramesh")
                .email("ramesh@gmail.com")
                .role("Developer")
                .build();

        ResultActions response = mockMvc.perform(put("/api/employees/{id}", savedEmployee.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedEmployee)));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(savedEmployee.getId())))
                .andExpect(jsonPath("$.name", is(updatedEmployee.getName())))
                .andExpect(jsonPath("$.email", is(updatedEmployee.getEmail())))
                .andExpect(jsonPath("$.role", is(updatedEmployee.getRole())));
    }

    @Test
    public void givenInvalidEmployeeId_whenUpdateEmployee_thenReturnNotFound() throws Exception {
        int invalidId = 999;
        Employee updatedEmployee = Employee.builder()
                .name("Ramesh")
                .email("ramesh@gmail.com")
                .role("Developer")
                .build();

        ResultActions response = mockMvc.perform(put("/api/employees/{id}", invalidId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedEmployee)));

        response.andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void givenEmployeeId_whenDeleteEmployee_thenReturnOk() throws Exception {
        Employee employee = createEmployee();
        Employee savedEmployee = employeeRepository.save(employee);

        ResultActions response = mockMvc.perform(delete("/api/employees/{id}", savedEmployee.getId()));

        response.andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void givenInvalidEmployeeId_whenDeleteEmployee_thenReturnNotFound() throws Exception {
        int invalidId = 999;

        ResultActions response = mockMvc.perform(delete("/api/employees/{id}", invalidId));

        response.andDo(print())
                .andExpect(status().isNotFound());
    }
    private Employee createEmployee() {
        return Employee.builder()
                .name("Ravi")
                .email("ravi@gmail.com")
                .role("Developer")
                .build();
    }
}
