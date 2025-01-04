package com.reliaquest.api.controller;

import com.reliaquest.api.model.CreateMockEmployeeInput;
import com.reliaquest.api.model.Employee;
import com.reliaquest.api.service.EmployeeService;
import com.reliaquest.api.exception.EmployeeNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class EmployeeControllerTest {

    @Mock
    private EmployeeService employeeService;

    @InjectMocks
    private EmployeeController employeeController;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(employeeController).build();
    }

    // Test Case 1: Get all employees
    @Test
    public void testGetAllEmployees() throws Exception {
        Employee employee1 = new Employee("20327fcc-c706-448d-ae59-ff358725fac6", "Rahul Rathod", 150000, 30, "Software Engineer", "mcshayne@company.com");
        Employee employee2 = new Employee("2b67a0b1-7f1e-4657-9a4d-826ca26fe2b2", "Edwardo Schroeder", 427942, 16, "Sales Supervisor", "imdahdude@company.com");

        List<Employee> employees = Arrays.asList(employee1, employee2);

        when(employeeService.getAllEmployees()).thenReturn(employees);

        mockMvc.perform(get("/api/employee"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].employee_name").value("Rahul Rathod"))
                .andExpect(jsonPath("$[1].employee_name").value("Edwardo Schroeder"));

        verify(employeeService, times(1)).getAllEmployees();
    }

    // Test Case 2: Get highest salary of employees
    @Test
    public void testGetHighestSalaryOfEmployees() throws Exception {
        when(employeeService.getHighestSalaryOfEmployees()).thenReturn(80000);

        mockMvc.perform(get("/api/employee/highestSalary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(80000));

        verify(employeeService, times(1)).getHighestSalaryOfEmployees();
    }

    // Test Case 3: Get top 10 highest earning employee names
    @Test
    public void testGetTopTenHighestEarningEmployeeNames() throws Exception {
        List<String> topEarnings = Arrays.asList("John", "Jane", "Alice");

        when(employeeService.getTop10HighestEarningEmployeeNames()).thenReturn(topEarnings);

        mockMvc.perform(get("/api/employee/topTenHighestEarningEmployeeNames"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(3))
                .andExpect(jsonPath("$[0]").value("John"))
                .andExpect(jsonPath("$[1]").value("Jane"))
                .andExpect(jsonPath("$[2]").value("Alice"));

        verify(employeeService, times(1)).getTop10HighestEarningEmployeeNames();
    }

    // Test Case 4: Get employees by name search
    @Test
    public void testGetEmployeesByNameSearch() throws Exception {
        Employee employee = new Employee("20327fcc-c706-448d-ae59-ff358725fac6", "Rahul Rathod", 150000, 30, "Software Engineer", "mcshayne@company.com");
        List<Employee> employees = Arrays.asList(employee);

        when(employeeService.getEmployeesByNameSearch("Rahul")).thenReturn(employees);

        mockMvc.perform(get("/api/employee/search/Rahul"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].employee_name").value("Rahul Rathod"));

        verify(employeeService, times(1)).getEmployeesByNameSearch("Rahul");
    }

    // Test Case 5: Get employee by ID
    @Test
    public void testGetEmployeeById_Success() throws Exception {
        Employee employee = new Employee("20327fcc-c706-448d-ae59-ff358725fac6", "Rahul Rathod", 150000, 30, "Software Engineer", "mcshayne@company.com");

        when(employeeService.getEmployeeById("20327fcc-c706-448d-ae59-ff358725fac6")).thenReturn(employee);

        mockMvc.perform(get("/api/employee/20327fcc-c706-448d-ae59-ff358725fac6"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.employee_name").value("Rahul Rathod"));

        verify(employeeService, times(1)).getEmployeeById("20327fcc-c706-448d-ae59-ff358725fac6");
    }

    // Test Case 6: Invalid UUID for employee ID
    @Test
    public void testGetEmployeeById_InvalidUUID() throws Exception {
        mockMvc.perform(get("/api/employee/invalid-uuid"))
                .andExpect(status().isBadRequest());
    }

    // Test Case 7: Employee not found by ID
    @Test
    public void testGetEmployeeById_NotFound() throws Exception {
        when(employeeService.getEmployeeById("20327fcc-c706-448d-ae59-ff358725fac6")).thenThrow(EmployeeNotFoundException.class);

        mockMvc.perform(get("/api/employee/20327fcc-c706-448d-ae59-ff358725fac6"))
                .andExpect(status().isNotFound());

        verify(employeeService, times(1)).getEmployeeById("20327fcc-c706-448d-ae59-ff358725fac6");
    }

    // Test Case 8: Create employee
    @Test
    public void testCreateEmployee() throws Exception {
        CreateMockEmployeeInput input = new CreateMockEmployeeInput("Rahul Rathod", 150000, "Software Engineer", 30);
        Employee createdEmployee = new Employee("b6b60215-9823-4232-abc5-11be0d2bde93", "Rahul Rathod", 150000, 30, "Software Engineer", "asoka@company.com");

        when(employeeService.createEmployee(any(CreateMockEmployeeInput.class))).thenReturn(createdEmployee);

        mockMvc.perform(post("/api/employee")
                        .contentType("application/json")
                        .content("{\"name\":\"Rahul Rathod\",\"salary\":150000,\"title\":\"Software Engineer\",\"age\":30}"))
                .andExpect(status().isCreated());

        verify(employeeService, times(1)).createEmployee(any(CreateMockEmployeeInput.class));
    }




    // Test Case 9: Delete employee by ID
    @Test
    public void testDeleteEmployeeById_Success() throws Exception {
        when(employeeService.deleteEmployeeById("20327fcc-c706-448d-ae59-ff358725fac6")).thenReturn("Employee with ID 20327fcc-c706-448d-ae59-ff358725fac6 has been deleted.");

        mockMvc.perform(delete("/api/employee/20327fcc-c706-448d-ae59-ff358725fac6"))
                .andExpect(status().isOk())
                .andExpect(content().string("Employee with ID 20327fcc-c706-448d-ae59-ff358725fac6 has been deleted."));

        verify(employeeService, times(1)).deleteEmployeeById("20327fcc-c706-448d-ae59-ff358725fac6");
    }

    @Test
    public void testDeleteEmployeeById_NotFound() throws Exception {
        when(employeeService.deleteEmployeeById("20327fcc-c706-448d-ae59-ff358725fac6")).thenThrow(EmployeeNotFoundException.class);

        mockMvc.perform(delete("/api/employee/20327fcc-c706-448d-ae59-ff358725fac6"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Employee not found with ID: 20327fcc-c706-448d-ae59-ff358725fac6"));

        verify(employeeService, times(1)).deleteEmployeeById("20327fcc-c706-448d-ae59-ff358725fac6");
    }
}
